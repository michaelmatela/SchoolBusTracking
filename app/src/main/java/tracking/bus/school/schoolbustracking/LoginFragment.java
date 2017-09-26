package tracking.bus.school.schoolbustracking;

import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import tracking.bus.school.schoolbustracking.Models.Profile;

public class LoginFragment extends Fragment {
    View view;

    private FirebaseAuth fireBaseAuth;
    private ProgressDialog progressDialog;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;

    Button backButton;
    Button loginButton;
    EditText etEmail;
    EditText etPassword;

    String email;
    String password;
    FloatingActionButton fab_menu;

    public LoginFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_login, container, false);

        fireBaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(getActivity());
        fab_menu = (FloatingActionButton) getActivity().findViewById(R.id.fab_menu);

        Firebase.setAndroidContext(getActivity());

        backButton = (Button) view.findViewById(R.id.btnBack);
        loginButton = (Button) view.findViewById(R.id.btnLogin);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeFragment();
                fab_menu.setVisibility(View.VISIBLE);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                login();
            }
        });

        return view;
    }

    public void login() {
        etEmail = (EditText) view.findViewById(R.id.etEmail);
        etPassword = (EditText) view.findViewById(R.id.etPassword);
        fab_menu.setVisibility(View.VISIBLE);
        email = etEmail.getText().toString();
        password = etPassword.getText().toString();
        progressDialog.setMessage("logging in Please Wait...");
        progressDialog.show();
        fireBaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (!user.isEmailVerified()){
                                user.sendEmailVerification()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    FirebaseAuth.getInstance().signOut();
                                                    Toast.makeText(getActivity(), "Login failed, please verify from your email, verification email sent to " + email, Toast.LENGTH_LONG).show();
                                                    removeFragment();
                                                    progressDialog.dismiss();
                                                }
                                            }
                                        });
                            }
                            else{
                                progressDialog.dismiss();
                                Toast.makeText(getActivity(), "You are now logged in.", Toast.LENGTH_LONG).show();
                                removeFragment();
                            }
                        }
                    }

                });
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
