package hu.beesmarter.bsmart.fontrecognizer.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import hu.beesmarter.bsmart.fontrecognizer.CameraUtils;
import hu.beesmarter.bsmart.fontrecognizer.analyzer.TessUtils;
import hu.beesmarter.bsmart.fontrecognizer.analyzer.basepoint.BasePointFontRecognizer;
import hu.beesmarter.bsmart.fontrecognizer.communication.ServerCommunicator;
import hu.beesmarter.bsmart.fontrecognizer.config.AppConfig;
import hu.beesmarter.bsmart.fontrecognizer.fontrecognizer.R;

public class MainActivity extends AppCompatActivity {

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		TessUtils.initTessdata(this);

		coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
		modeSwitch = (Switch) findViewById(R.id.mode_switch);
		testModeLayout = findViewById(R.id.layout_mode_test);
		realModeLayout = findViewById(R.id.layout_mode_real);
		testInputIpAddrss = (EditText) findViewById(R.id.input_value_ip);
		testStartCommunicationButton = (Button) findViewById(R.id.start_communication);
		realStartCameraButton = (Button) findViewById(R.id.start_camera_button);
		realResultText = (TextView) findViewById(R.id.font_result_text);
		realStatusMessage = (TextView) findViewById(R.id.status_message);

		if (savedInstanceState != null) {
			modeSwitch.setChecked(savedInstanceState.getBoolean(STATE_MODE));
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
				startCommunication();
			}
		});

		realStartCameraButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startCamera();
			}
		});

		initMode(modeSwitch.isChecked());
	}

	private void initMode(boolean isChecked) {
		if (isChecked) {
			testMode();
		} else {
			realMode();
		}
	}

	private void startCamera() {
		Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, CameraUtils.getTakenImageUri());
		startActivityForResult(cameraIntent, REQUEST_CAMERA_RESULT_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == REQUEST_CAMERA_RESULT_CODE) {
			Bitmap capturedImage = CameraUtils.getSavedBitmapNormalized();
			processCapturedImage(capturedImage);
		}
	}

	private void processCapturedImage(Bitmap capturedImage) {
		String fontName = new BasePointFontRecognizer()
				.recognizeFontFromImage(capturedImage).getFontName();
		realStatusMessage.setVisibility(View.VISIBLE);
		realResultText.setText(fontName);

	}

	private void startCommunication() {
		String ipAddress = testInputIpAddrss.getText().toString();
		if (ipAddress.equals("")) {
			showMessage("Please set an IP!");
			return;
		}
		try {
			ServerCommunicator serverCommunicator = new ServerCommunicator(ipAddress, AppConfig.COMM_PORT);
			serverCommunicator.startCommunication();
			serverCommunicator.helloServer();

			serverCommunicator.endCommuncation();
		} catch (IOException | ExecutionException | InterruptedException e) {
			showMessage("Exception while communicating with server!");
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
		savedInstanceState.putBoolean(STATE_MODE, modeSwitch.isChecked());

		super.onSaveInstanceState(savedInstanceState);
	}
}
