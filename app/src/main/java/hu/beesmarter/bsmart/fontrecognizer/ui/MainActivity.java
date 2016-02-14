package hu.beesmarter.bsmart.fontrecognizer.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import hu.beesmarter.bsmart.fontrecognizer.analyzer.CharacterItem;
import hu.beesmarter.bsmart.fontrecognizer.analyzer.Font;
import hu.beesmarter.bsmart.fontrecognizer.analyzer.FontRecognizer;
import hu.beesmarter.bsmart.fontrecognizer.analyzer.TessUtils;
import hu.beesmarter.bsmart.fontrecognizer.analyzer.basepoint.BasePointFontRecognizer;
import hu.beesmarter.bsmart.fontrecognizer.analyzer.compare.CompareFontRecognizer;
import hu.beesmarter.bsmart.fontrecognizer.communication.ServerCommunicator;
import hu.beesmarter.bsmart.fontrecognizer.config.AppConfig;
import hu.beesmarter.bsmart.fontrecognizer.fontrecognizer.R;
import hu.beesmarter.bsmart.fontrecognizer.typeface.TypeFaceManager;
import hu.beesmarter.bsmart.fontrecognizer.util.CameraUtils;
import hu.beesmarter.bsmart.fontrecognizer.util.ImageUtils;

public class MainActivity extends BaseActivity implements TypeFaceManager.TypefaceLoadingListener{

	private static final String STATE_FONT_RECOGNIZER = "STATE_FONT_RECOGNIZER";

	private static final String STATE_MODE = "state_mode";
	private static final int REQUEST_CAMERA_RESULT_CODE = 5;

	private CoordinatorLayout coordinatorLayout;

	private Switch modeSwitch;

	private View testModeLayout;

	private View realModeLayout;

	private EditText testInputIpAddrss;

	private Button testStartCommunicationButton;

	private Button realStartCameraButton;

	private TextView realStatusMessage;

	private TextView realResultText;

	//TODO for test, please remove it before the release
	private ImageView imageView;

	private FontRecognizer fontRecognizer = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
		modeSwitch = (Switch) findViewById(R.id.mode_switch);
		testModeLayout = findViewById(R.id.layout_mode_test);
		realModeLayout = findViewById(R.id.layout_mode_real);
		testInputIpAddrss = (EditText) findViewById(R.id.input_value_ip);
		testStartCommunicationButton = (Button) findViewById(R.id.start_communication);
		realStartCameraButton = (Button) findViewById(R.id.start_camera_button);
		realResultText = (TextView) findViewById(R.id.font_result_text);
		realStatusMessage = (TextView) findViewById(R.id.status_message);
		imageView = (ImageView)findViewById(R.id.image);

		if (savedInstanceState != null) {
			modeSwitch.setChecked(savedInstanceState.getBoolean(STATE_MODE));
			fontRecognizer = (FontRecognizer) savedInstanceState.getSerializable(STATE_FONT_RECOGNIZER);
		}

		modeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				initMode(isChecked);
			}
		});

		testStartCommunicationButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (fontRecognizer == null) {
					return;
				}
				startCommunication();
			}
		});

		realStartCameraButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (fontRecognizer == null) {
					return;
				}
				startCamera();
			}
		});

		initMode(modeSwitch.isChecked());

		if (fontRecognizer == null) {
			TypeFaceManager.loadTypeFaces(this, this);
		}

	}

	@Override
	protected void onPermissionsGranted() {
		TessUtils.initTessdata(this);
	}

	private void initMode(boolean isChecked) {
		if (isChecked) {
			testMode();
		} else {
			realMode();
		}
	}

	private void startCamera() {
		Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, CameraUtils.getTakenImageUri());
		startActivityForResult(cameraIntent, REQUEST_CAMERA_RESULT_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == REQUEST_CAMERA_RESULT_CODE) {
			Bitmap capturedImage = CameraUtils.getSavedBitmapProcessed();
			CameraUtils.saveCameraPicture(this, capturedImage, "captured_image");
			processCapturedImage(capturedImage);
		}
	}

	private void processCapturedImage(Bitmap capturedImage) {
		BasePointFontRecognizer basePointFontRecognizer = new BasePointFontRecognizer();
//		String fontName =
//				basePointFontRecognizer.recognizeFontFromImage(capturedImage).getFontName();
		List<CharacterItem> characters = basePointFontRecognizer.getCharactersBitmap(capturedImage);

		//TODO for test, please remove
		imageView.setImageBitmap(characters.get(2).getBitmap());
		realResultText.setText(String.valueOf(characters.get(2).getCharacter()));

	}

	private void startCommunication() {
		final String ipAddress = testInputIpAddrss.getText().toString();
		if (ipAddress.equals("")) {
			showMessage("Please set an IP!");
			return;
		}
		try {
			if (!new AsyncTask<Void, Void, Boolean>() {
				@Override
				protected Boolean doInBackground(Void... params) {
					try {
						ServerCommunicator serverCommunicator =
								new ServerCommunicator(ipAddress, AppConfig.COMM_PORT);
						if (!serverCommunicator.startCommunication()) {
							return false;
						}
						int remainingPictures = serverCommunicator.helloServer();
						while (remainingPictures > 0) {
							//Font font = fontRecognizer.recognizeFontFromImage(
							//		ImageUtils.processImage(serverCommunicator.getNextPicture()));
							Font font = fontRecognizer.recognizeFontFromImage(serverCommunicator.getNextPicture());

							serverCommunicator.sendFont(font);
							--remainingPictures;
						}
						serverCommunicator.endCommunication();
					} catch (IOException e) {
						return false;
					}
					return true;
				}
			}.execute().get()) {
				throw new IllegalStateException();
			}
		} catch (InterruptedException | ExecutionException | IllegalStateException e) {
			e.printStackTrace();
			showMessage("Something went wrong. Please try again.");
		}
	}

	private void realMode() {
		realModeLayout.setVisibility(View.VISIBLE);
		testModeLayout.setVisibility(View.GONE);

	}

	private void testMode() {
		realModeLayout.setVisibility(View.GONE);
		testModeLayout.setVisibility(View.VISIBLE);

	}

	public void showMessage(String message) {
		Snackbar snackbar = Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG);
		snackbar.show();
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putBoolean(STATE_MODE, modeSwitch.isChecked());
		if (fontRecognizer != null) {
			savedInstanceState.putSerializable(STATE_FONT_RECOGNIZER, fontRecognizer);
		}
	}

	@Override
	public void typefacesLoaded(List<Pair<Typeface, Font>> typefaces) {
		fontRecognizer = new CompareFontRecognizer(typefaces);
	}

	@Override
	public void typefacesFailedToLoad() {
		fontRecognizer = new BasePointFontRecognizer();
	}
}
