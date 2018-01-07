package tracking.bus.school.schoolbustracking;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import tracking.bus.school.schoolbustracking.Models.Profile;

import static android.app.Activity.RESULT_OK;


public class RegisterFragment extends Fragment {
    View view;

    private FirebaseAuth fireBaseAuth;
    private ProgressDialog progressDialog;

    Spinner spinner;
    Button backButton;
    Button registerButton;
    EditText etFullName;
    EditText etPhoneNumber;
    EditText etEmail;
    EditText etPassword;
    EditText etRePassword;
    ImageView ivPic;

    String fullName;
    String phoneNumber;
    String email;
    String password;
    String rePassword;
    String userAccess;
    FloatingActionButton fab_menu;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_register, container, false);

        registerButton = (Button) view.findViewById(R.id.btnRegister);
        backButton = (Button) view.findViewById(R.id.btnBack);
        fab_menu = (FloatingActionButton) getActivity().findViewById(R.id.fab_menu);

        fireBaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(getActivity());

        Firebase.setAndroidContext(getActivity());

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register(view);
            }
        });
ivPic = (ImageView) view.findViewById(R.id.ivPic);
        ivPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , 1);//one can be replaced with any action code
            }
        });

        backButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                removeFragment();
                fab_menu.setVisibility(View.VISIBLE);
            }
        });
        return view;
    }

    public void register(View view){
        etEmail = (EditText) view.findViewById(R.id.etEmail);
        etFullName = (EditText) view.findViewById(R.id.etFullName);
        etPhoneNumber = (EditText) view.findViewById(R.id.etPhoneNumber);
        etPassword = (EditText) view.findViewById(R.id.etPassword);
        etRePassword = (EditText) view.findViewById(R.id.etRePassword);
        spinner = (Spinner) view.findViewById(R.id.spinner);

        email = etEmail.getText().toString();
        fullName = etFullName.getText().toString();
        phoneNumber = etPhoneNumber.getText().toString();
        password = etPassword.getText().toString();
        rePassword = etRePassword.getText().toString();
        userAccess = spinner.getSelectedItem().toString();

        if (userAccess.equals("Admin"))
            userAccess = "3";
        else if(userAccess.equals("Driver"))
            userAccess = "1";
        else if(userAccess.equals("Parent"))
            userAccess = "2";
        else {
            Toast.makeText(getActivity(), "Please choose desired user access.", Toast.LENGTH_LONG).show();
            return;
        }
        if(email.isEmpty() || fullName.isEmpty() || password.isEmpty() || rePassword.isEmpty() || phoneNumber.isEmpty()){
            Toast.makeText(getActivity(), "Please fill required fields.", Toast.LENGTH_LONG).show();
        } else{
            if (!password.equals(rePassword))
                Toast.makeText(getActivity(), "Password does not match.", Toast.LENGTH_LONG).show();
            else {
                if (ivPic.getTag() != null) {
                    progressDialog.setMessage("Registering Please Wait...");
                    progressDialog.show();

                    //creating a new user
                    fireBaseAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    //checking if success
                                    if (task.isSuccessful()) {
                                        //display some message here

                                        fireBaseAuth.signInWithEmailAndPassword(email, password)
                                                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                                        if (task.isSuccessful()) {

                                                            Firebase ref = new Firebase(Config.FIREBASE_URL);
                                                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                                            Profile profile = new Profile();

                                                            profile.setFullName(fullName);
                                                            profile.setPhoneNumber(phoneNumber);
                                                            profile.setEmail(email);
                                                            profile.setPassword(password);
                                                            profile.setType(userAccess);

                                                            ref.child("Profile").child(user.getUid()).setValue(profile);


                                                            String path = ivPic.getTag().toString();
                                                            Uri uri = Uri.parse(path);
                                                            FirebaseStorage storage = FirebaseStorage.getInstance();
                                                            StorageReference storageRef = storage.getReference().child("Profile").child(user.getUid());
                                                            storageRef.putFile(uri);

                                                            user.sendEmailVerification()
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()) {
                                                                                FirebaseAuth.getInstance().signOut();
                                                                                Toast.makeText(getActivity(), "Registration successful, verification email sent to " + email, Toast.LENGTH_LONG).show();
                                                                                removeFragment();
                                                                                fab_menu.setVisibility(View.VISIBLE);
                                                                            }
                                                                        }
                                                                    });
                                                        }
                                                        progressDialog.dismiss();
                                                    }
                                                });
                                        progressDialog.dismiss();
                                    } else {
                                        //display some message here
                                        Toast.makeText(getActivity(), "Registration Error", Toast.LENGTH_LONG).show();
                                    }
                                    progressDialog.dismiss();
                                }
                            });
                }
                else{
                    Toast.makeText(getActivity(), "Please choose a photo for this account", Toast.LENGTH_LONG).show();
                }
            }
        }

    }


    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case 0:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    ivPic.setImageURI(selectedImage);
                    ivPic.setTag(selectedImage);
                }

                break;
            case 1:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    ivPic.setImageURI(selectedImage);
                    ivPic.setTag(selectedImage);
                }
                break;
        }
    }


    public void removeFragment(){
        List<Fragment> fragments = getActivity().getSupportFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment != null)
                getActivity().getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            }
        }
    }
}
