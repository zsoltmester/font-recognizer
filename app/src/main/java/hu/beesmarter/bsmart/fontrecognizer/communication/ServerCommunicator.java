package hu.beesmarter.bsmart.fontrecognizer.communication;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ExecutionException;

import hu.beesmarter.bsmart.fontrecognizer.analyzer.FontObject;
import hu.beesmarter.bsmart.fontrecognizer.config.AppConfig;
import hu.beesmarter.bsmart.fontrecognizer.config.CommunicationConfig;


public class ServerCommunicator {

	private static final String ENCODING = "ISO-8859-1";

	private Socket socket;
	private PrintWriter pw;
	private BufferedReader br;
	private String dataId;
	private int port;
	private String address;
	private boolean socketEnabled;

	public ServerCommunicator(final String address, final int port, final String dataId) throws IOException, ExecutionException, InterruptedException {
		this.dataId = dataId;
		this.address = address;
		this.port = port;
	}

	public Boolean startCommunication() throws ExecutionException, InterruptedException {
		boolean result = new AsyncTask<Void, Void, Boolean>() {
			@Override
			protected Boolean doInBackground(Void... params) {
				try {
					socket = new Socket(address, port);
					InputStream inputStream = socket.getInputStream();
					OutputStream outputStream = socket.getOutputStream();
					br = new BufferedReader(new InputStreamReader(inputStream, ENCODING));
					outputStream = new BufferedOutputStream(outputStream);
					pw = new PrintWriter(new OutputStreamWriter(outputStream, ENCODING));
					socketEnabled = true;
					return socketEnabled;
				} catch (IOException e) {
					e.printStackTrace();
				}
				return socketEnabled;
			}
		}.execute().get();
		Log.d(AppConfig.LOG, "Connection succeed: " + String.valueOf(result));
		return result;
	}

	public Boolean endCommuncation() throws IOException, ExecutionException, InterruptedException {
		return new AsyncTask<Void, Void, Boolean>() {
			@Override
			protected Boolean doInBackground(Void... params) {
				try {
					socket.close();
					socketEnabled = false;
				} catch (IOException e) {
					e.printStackTrace();
				}
				return true;
			}
		}.execute().get();

	}

	/**
	 * Gets back how money picture will be sent by the server.
	 *
	 * @return how money pictures will be sent by the server.
	 * @throws ExecutionException   please catch it.
	 * @throws InterruptedException please catch it.
	 */
	public int helloServer() throws ExecutionException, InterruptedException {
		return new AsyncTask<String, Void, Integer>() {
			@Override
			protected Integer doInBackground(String... params) {
				String message = getMessage();
				if (message.equals(CommunicationConfig.COMM_MSG_HELLO_SERVER)) {
					sendMessage(CommunicationConfig.COMM_MSG_HELLO_CLIENT);
				}
				message = getMessage(); //SEND YOUR ID
				if (message.equals(CommunicationConfig.COMM_MSG_REQUEST_ASK_ID)) {
					sendMessage(CommunicationConfig.COMM_MSG_REPLY_ASK_ID);
				}
				message = getMessage();
				return getRemainingPictures(message);
			}
		}.execute().get();

	}

	public Bitmap getNextPicture() throws ExecutionException, InterruptedException {
		return new AsyncTask<Void, Void, Bitmap>() {
			@Override
			protected Bitmap doInBackground(Void... params) {
				sendMessage(CommunicationConfig.COMM_MSG_CLIENT_REQUEST_NEXT_PICTURE);
				String response = getMessage();
				byte[] imageInBytes = ImageEncoderUtils.convertToHex(formatResponseToPictureBytes(response));
				return ImageEncoderUtils.convertToBitmap(imageInBytes);
			}
		}.execute().get();
	}

	/**
	 * Sends the thought font to the server and gets back how money pictures remained.
	 *
	 * @param fontObject the fontObject
	 * @return gets back the reamining pictures, or 0 if there is no more.
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	public int sendThoughtFont(final FontObject fontObject) throws ExecutionException, InterruptedException {
		;
		return new AsyncTask<String, Void, Integer>() {

			@Override
			protected Integer doInBackground(String... params) {
				sendMessage(fontObject.getFontName());
				String response = getMessage();
				return getRemainingPictures(response);
			}
		}.execute().get();
	}

	private void sendMessage(String message) {
		pw.println(message);
		pw.flush();
	}

	private String getMessage() {
		String message = null;
		try {
			message = br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (message != null)
			Log.d(AppConfig.LOG, message);
		return message;
	}

	public boolean checkEndMessage(String message) {
		return message.startsWith(CommunicationConfig.COMM_MSG_SERVER_FONT_ACK_END);
	}

	public int getRemainingPictures(String communicationMessage) {
		int indexBeforeNumber = communicationMessage.lastIndexOf(": ");
		if (indexBeforeNumber == -1) {
			return 0;
		}
		String splittedString = communicationMessage.substring(indexBeforeNumber + 1);
		return Integer.valueOf(splittedString);
	}


	/**
	 * Cuts the first >> and last << from the string.
	 *
	 * @param imageResponse the server response containing the image.
	 * @return the formatted string.
	 */
	private String formatResponseToPictureBytes(String imageResponse) {
		String formattedString = imageResponse.substring(2, imageResponse.length() - 1 - 2);
		Log.d(AppConfig.LOG, formattedString);
		return formattedString;
	}
}
