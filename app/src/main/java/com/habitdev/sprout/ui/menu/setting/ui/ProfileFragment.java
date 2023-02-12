package com.habitdev.sprout.ui.menu.setting.ui;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.habitdev.sprout.R;
import com.habitdev.sprout.database.user.UserViewModel;
import com.habitdev.sprout.database.user.model.User;
import com.habitdev.sprout.databinding.FragmentProfileBinding;
import com.habitdev.sprout.enums.SettingConfigurationKeys;
import com.habitdev.sprout.utill.DateTimeElapsedUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 101;
    private static String selectedProfilePath;
    private static boolean onCustomProfile;
    private static User user;
    private static UserViewModel userViewModel;

    public interface OnReturnSetting {
        void returnFromProfileToSetting();
    }

    private OnReturnSetting mOnReturnSetting;

    public void setmOnReturnSetting(OnReturnSetting mOnReturnSetting) {
        this.mOnReturnSetting = mOnReturnSetting;
    }

    public ProfileFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        user = userViewModel.getUserByUID(1);

        userViewModel.getUserNickname().observe(getViewLifecycleOwner(), nickname -> {
            binding.settingProfileChangeNickname.setText(nickname);
        });

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(SettingConfigurationKeys.SETTING_SHAREDPRED.getKey(), Context.MODE_PRIVATE);
        if (!sharedPreferences.getAll().isEmpty()) {
            onCustomProfile = sharedPreferences.getBoolean(SettingConfigurationKeys.IS_CUSTOM_PROFILE.getKey(), onCustomProfile);

            if (onCustomProfile) {

                selectedProfilePath = sharedPreferences.getString(SettingConfigurationKeys.CUSTOM_PROFILE_PATH.getKey(), null);

                if (selectedProfilePath != null) {
                    Bitmap profile = BitmapFactory.decodeFile(selectedProfilePath); // checks if the photo still exist in gallery
                    if (profile != null) {
                        binding.settingProfileImgView.setVisibility(View.VISIBLE);
                        binding.settingProfileLottieAvatar.setVisibility(View.GONE);
                        binding.settingProfileImgView.setImageBitmap(profile);
                    } else {
                        setDefaultProfile();
                    }
                }
            }
        } else {
            setDefaultProfile();
        }

        binding.settingChangeProfilePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkSelfPermission(requireActivity())) {
                    chooseImage(requireActivity());
                }
            }
        });

        binding.settingProfileIdentityLbl.setText(user.getIdentity());
        setTimeElapsedSinceInstalled();
        validate_nickname();

        binding.settingProfileSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String currentProfilePath = requireActivity().getSharedPreferences(SettingConfigurationKeys.SETTING_SHAREDPRED.getKey(), Context.MODE_PRIVATE).getString(SettingConfigurationKeys.CUSTOM_PROFILE_PATH.getKey(), "");

                Log.d("tag", "onClick: " + currentProfilePath + ":" + selectedProfilePath);

                if ((binding.settingProfileChangeNicknameHint.getText().toString().trim().isEmpty() && !binding.settingProfileChangeNickname.getText().toString().trim().equals(user.getNickname())) && (selectedProfilePath != null && !selectedProfilePath.trim().isEmpty() && !selectedProfilePath.equals(currentProfilePath))) {
                    showConfirmUpdateDialog(0);
                } else if ((binding.settingProfileChangeNicknameHint.getText().toString().trim().isEmpty() && !binding.settingProfileChangeNickname.getText().toString().trim().equals(user.getNickname()))) {
                   showConfirmUpdateDialog(1);
                } else if (selectedProfilePath != null && !selectedProfilePath.trim().isEmpty() && !selectedProfilePath.equals(currentProfilePath)) {
                    showConfirmUpdateDialog(2);
                } else {
                    // do not show prompt
                }
            }
        });

        onBackPress();
        return binding.getRoot();
    }

    public void showConfirmUpdateDialog(int updateType) {
        String[] message = {
            "Do you want to apply changes for nickname and profile photo?",
            "Do you want to apply changes for nickname?",
            "Do you want to appy changes for profile photo"
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setMessage(message[updateType])
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        switch (updateType) {
                            case 0:
                                updateNickname();
                                updateProfile();
                                break;
                            case 1:
                                updateNickname();
                                break;
                            case 2:
                                updateProfile();
                                break;
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Perform action for "No" button click
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void updateNickname(){
        user.setNickname(binding.settingProfileChangeNickname.getText().toString().trim());
        userViewModel.update(user);
    }

    private void updateProfile(){
        requireActivity().getSharedPreferences(SettingConfigurationKeys.SETTING_SHAREDPRED.getKey(), Context.MODE_PRIVATE)
                .edit()
                .putBoolean(SettingConfigurationKeys.IS_CUSTOM_PROFILE.getKey(), onCustomProfile)
                .putString(SettingConfigurationKeys.CUSTOM_PROFILE_PATH.getKey(), selectedProfilePath)
                .apply();
        selectedProfilePath = "";
    }

    private void validate_nickname() {
        final String REQUIRED = "Required*";
        final String MIN_MAX_CHARACTERS = "Minimum of 3, Maximum of 15 Characters*";
        final String INVALID = "Invalid nickname*";

        binding.settingProfileChangeNickname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().isEmpty()){
                    binding.settingProfileChangeNicknameHint.setText(REQUIRED);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().trim().isEmpty()){
                    binding.settingProfileChangeNicknameHint.setText(REQUIRED);
                } else if (editable.toString().trim().length() > 15 || editable.toString().trim().length() < 3 ) {
                    binding.settingProfileChangeNicknameHint.setText(MIN_MAX_CHARACTERS);
                } else if (!Pattern.compile("^[a-zA-Z ]*$").matcher(editable.toString().trim()).matches()){
//            Allowed Input a-zA-Z space
                    binding.settingProfileChangeNicknameHint.setText(INVALID);
                } else {
                    binding.settingProfileChangeNicknameHint.setText("");
                }
            }
        });
    }

    private void setTimeElapsedSinceInstalled() {
        String date = user.getDateInstalled();
        DateTimeElapsedUtil dateTimeElapsedUtil = new DateTimeElapsedUtil(date);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (binding != null) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        dateTimeElapsedUtil.calculateElapsedDateTime();
                        try {
                            binding.settingProfileDateInstalledLbl.setText(dateTimeElapsedUtil.getResult());
                        } catch (Exception e) {
                            //prevent null binding
                            e.printStackTrace();
                        }
                    });
                } else {
                    timer.cancel();
                    timer.purge();
                }
            }
        }, 0, 1000);
    }

    private void setDefaultProfile() {
        final String[] default_male_profiles = {
                "default_user_profile_male-avatar.json",
                "default_user_profile_male-avatar-v1.json",
                "default_user_profile_male-avatar-v2.json",
                "default_user_profile_male-avatar-v3.json",
                "default_user_profile_male-avatar-v4.json"
        };

        final String[] default_female_profiles = {
                "default_user_profile_female-avatar.json",
                "default_user_profile_female-avatar-v1.json",
                "default_user_profile_female-avatar-v2.json",
                "default_user_profile_female-avatar-v3.json",
                "default_user_profile_female-avatar-v4.json",
                "default_user_profile_female-avatar-v5.json"
        };

        final String[] default_non_binary_profiles = Stream.concat(Arrays.stream(default_male_profiles), Arrays.stream(default_female_profiles)).toArray(String[]::new);

        String identity = user.getIdentity();

        binding.settingProfileLottieAvatar.setVisibility(View.VISIBLE);
        binding.settingProfileImgView.setVisibility(View.GONE);
        switch (identity != null ? identity: "Default") {
            case "Male":
                binding.settingProfileLottieAvatar.setAnimation(default_male_profiles[new Random().nextInt(default_male_profiles.length)]);
                break;
            case "Female":
                binding.settingProfileLottieAvatar.setAnimation(default_female_profiles[new Random().nextInt(default_female_profiles.length)]);
                break;
            default:
                binding.settingProfileLottieAvatar.setAnimation(default_non_binary_profiles[new Random().nextInt(default_non_binary_profiles.length)]);
                break;
        }
    }

    // function to check permission
    public static boolean checkSelfPermission(final Activity activity) {
        int WriteExternastoragePermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int CameraPermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);

        List<String> permmisionRequired = new ArrayList<>();

        if (WriteExternastoragePermission != PackageManager.PERMISSION_GRANTED) {
            permmisionRequired.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (CameraPermission != PackageManager.PERMISSION_GRANTED) {
            permmisionRequired.add(Manifest.permission.CAMERA);
        }

        if (!permmisionRequired.isEmpty()) {
            ActivityCompat.requestPermissions(activity, permmisionRequired.toArray(new String[permmisionRequired.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_ID_MULTIPLE_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(requireContext(), "FlagUp Requires Access to Camara.", Toast.LENGTH_SHORT).show();
            } else if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(requireContext(), "FlagUp Requires Access to Your Storage.", Toast.LENGTH_SHORT).show();
            } else {
                chooseImage(requireActivity());
            }
        }
    }

    // function to let's the user to choose image from camera or gallery
    private void chooseImage(Context context){
        final CharSequence[] optionsMenu = {"Take Photo", "Choose from Gallery", "Default Profile", "Exit" }; // create a menuOption Array
        // create a dialog for showing the optionsMenu
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // set the items in builder.
        builder.setItems(optionsMenu, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(optionsMenu[i].equals("Take Photo")){
                    // Open the camera and get the photo
                    Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, 0);
                }
                else if(optionsMenu[i].equals("Choose from Gallery")){
                    // choose from  external storage
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto , 1);
                }
                else if(optionsMenu[i].equals("Default Profile")) {
                    requireActivity().getSharedPreferences(SettingConfigurationKeys.SETTING_SHAREDPRED.getKey(), Context.MODE_PRIVATE)
                            .edit().clear().apply();
                    onCustomProfile = false;
                    selectedProfilePath = "";
                    setDefaultProfile();
                }
                else if (optionsMenu[i].equals("Exit")) {
                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0:
                    if (resultCode == RESULT_OK && data != null) {
                        Bitmap photo = (Bitmap) data.getExtras().get("data");
                        binding.settingProfileImgView.setImageBitmap(photo);
                        //camera capture get path and save on room instead
                        binding.settingProfileLottieAvatar.setVisibility(View.GONE);
                        binding.settingProfileImgView.setVisibility(View.VISIBLE);

                        // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                        photo.compress(Bitmap.CompressFormat.JPEG, 100, bytes); //thumbnail
//                        photo = Bitmap.createScaledBitmap(photo, 1000, 1000,true); //original image

                        String path = MediaStore.Images.Media.insertImage(requireActivity().getContentResolver(), photo, "Sprout_CapturedPhoto", null);
                        Uri capturedImageUri = Uri.parse(path);

                        selectedProfilePath = getRealPathFromURI(capturedImageUri);

                        onCustomProfile = true;
                    }
                    break;
                case 1:
                    if (resultCode == RESULT_OK && data != null) {
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        if (selectedImage != null) {
                            Cursor cursor = requireActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                            if (cursor != null) {
                                cursor.moveToFirst();
                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                String picturePath = cursor.getString(columnIndex);
                                selectedProfilePath = picturePath;
                                binding.settingProfileImgView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                                binding.settingProfileLottieAvatar.setVisibility(View.GONE);
                                binding.settingProfileImgView.setVisibility(View.VISIBLE);
                                onCustomProfile = true;
                                cursor.close();
                            }
                        }
                    }
                    break;
            }
        }
    }

    public String getRealPathFromURI(Uri uri) {
        String path = "";
        if (requireActivity().getContentResolver() != null) {
            Cursor cursor = requireActivity().getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
        }
        return path;
    }

    private void onBackPress() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (mOnReturnSetting != null) mOnReturnSetting.returnFromProfileToSetting();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}