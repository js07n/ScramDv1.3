package edu.fsu.cs.scramd.camera;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.SaveCallback;

import edu.fsu.cs.scramd.R;

import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.GradientDrawable;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


public class CameraFragment extends Fragment {

	public static final String TAG = "CameraFragment";

	private Camera camera;
	private SurfaceView surfaceView;
	private ParseFile photoFile;
	private Button photoButton;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
			Bundle savedInstanceState){
		View v = inflater.inflate(R.layout.fragment_camera, parent, false);
		
		photoButton = (Button) v.findViewById(R.id.camera_photo_button);
	
		if (camera == null) {
			try {

				camera = Camera.open();

				photoButton.setEnabled(true);
			} catch (Exception e) {
				Log.e(TAG, "No camera with exception: " + e.getMessage());
				photoButton.setEnabled(false);
				Toast.makeText(getActivity(), "No camera detected",
						Toast.LENGTH_LONG).show();
			}
		}

		
		// **************************************
		// * PhotoButton Listener
		// **************************************
		photoButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (camera == null)
					return;
				camera.takePicture(new Camera.ShutterCallback() {

					@Override
					public void onShutter() {
						// nothing to do
					}

				}, null, new Camera.PictureCallback() {

					@Override
					public void onPictureTaken(byte[] data, Camera camera) {
						saveScaledPhoto(data);
						//???
						
					}

				});

			}
		});
		
		// **************************************
		// * Surface stuff. Does Things.
		// **************************************
		
		surfaceView = (SurfaceView) v.findViewById(R.id.camera_surface_view);
		

		
		SurfaceHolder holder = surfaceView.getHolder();
		holder.addCallback(new Callback() {

			public void surfaceCreated(SurfaceHolder holder) {
				try {
					if (camera != null) {
						camera.setDisplayOrientation(90);
							
						Camera.Parameters params= camera.getParameters();
						   surfaceView.getLayoutParams().width=params.getPreviewSize().height;
						   surfaceView.getLayoutParams().height=params.getPreviewSize().width;
						camera.setPreviewDisplay(holder);
						camera.startPreview();
					}
				} catch (IOException e) {
					Log.e(TAG, "Error setting up preview", e);
				}
			}

			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {
				// nothing to do here
			
			}

			public void surfaceDestroyed(SurfaceHolder holder) {
				// nothing here
			}

		});

		return v;
	
	}
	
	/*
	 * ParseQueryAdapter loads ParseFiles into a ParseImageView at whatever size
	 * they are saved. Since we never need a full-size image in our app, we'll
	 * save a scaled one right away.
	 */
	private void saveScaledPhoto(byte[] data) {

		// Resize photo from camera byte array
		Bitmap scramdImage = BitmapFactory.decodeByteArray(data, 0, data.length);
		Bitmap scramdImageScaled = Bitmap.createScaledBitmap(scramdImage, 480, 360
				//* scramdImage.getHeight() / scramdImage.getWidth(), false);
				,false);

		// Override Android default landscape orientation and save portrait
		Matrix matrix = new Matrix();
		matrix.postRotate(90);
/*
		Bitmap rotatedScaledScramdImage = Bitmap.createBitmap(scramdImageScaled, 0,
				0, scramdImageScaled.getWidth(), scramdImageScaled.getHeight(),
				matrix, true);
*/
		Bitmap rotatedScaledScramdImage = Bitmap.createBitmap(scramdImageScaled, (scramdImageScaled.getWidth()/4),
				0, scramdImageScaled.getHeight(), scramdImageScaled.getHeight(),
				matrix, true);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		rotatedScaledScramdImage.compress(Bitmap.CompressFormat.JPEG, 100, bos);

		byte[] scaledData = bos.toByteArray();

		// Save the scaled image to Parse
		photoFile = new ParseFile("scramd_photo.jpg", scaledData);
		photoFile.saveInBackground(new SaveCallback() {

			public void done(ParseException e) {
				if (e != null) {
					Toast.makeText(getActivity(),
							"Error saving: " + e.getMessage(),
							Toast.LENGTH_LONG).show();
				} else {
					addPhotoToAccountAndReturn(photoFile);
				}
			}
		});
	}
	
	/*
	 * Once the photo has saved successfully, we're ready to return to the
	 * NewScramdFragment. When we added the CameraFragment to the back stack, we
	 * named it "CameraConfirmFragment". Now we'll pop fragments off the back stack
	 * until we reach that Fragment.
	 */
	private void addPhotoToAccountAndReturn(ParseFile photoFile) {
		((CameraActivity) getActivity()).getCurrentAccount().setPhotoFile(
				photoFile);
		FragmentManager fm = getActivity().getFragmentManager();
		fm.popBackStack("CameraConfirmFragment",
				FragmentManager.POP_BACK_STACK_INCLUSIVE);
	}

	@Override
	public void onResume() {
		
		if (camera == null) {
			try {
				camera = Camera.open();
				photoButton.setEnabled(true);
			} catch (Exception e) {
				Log.i(TAG, "No camera: " + e.getMessage());
				photoButton.setEnabled(false);
				Toast.makeText(getActivity(), "No camera detected",
						Toast.LENGTH_LONG).show();
			}
		}
		super.onResume();
	}

	@Override
	public void onPause() {
		//super.onPause();
		if (camera != null) {
			camera.stopPreview();
			camera.release();
		}
		super.onPause();
	}

}
