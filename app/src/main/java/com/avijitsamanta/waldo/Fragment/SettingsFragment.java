package com.avijitsamanta.waldo.Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.avijitsamanta.waldo.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsFragment extends Fragment {
    private FirebaseAuth auth;
    private static final int GOOGLE_SIGN_IN_CODE = 101;
    private GoogleSignInClient googleSignInClient;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        auth = FirebaseAuth.getInstance();
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        CircleImageView circleImageView = view.findViewById(R.id.profile_image);
        LinearLayout login = view.findViewById(R.id.login);
        TextView tVLoginStatus = view.findViewById(R.id.tv_loginStatus);
        progressBar = view.findViewById(R.id.progress_bar_profile);
        TextView name = view.findViewById(R.id.name);
        TextView email = view.findViewById(R.id.email);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(Objects.requireNonNull(getActivity()), gso);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (auth.getCurrentUser() != null) {
                    progressBar.setVisibility(View.VISIBLE);
                    auth.signOut();
                    googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressBar.setVisibility(View.GONE);
                            Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingsFragment())
                                    .commit();
                        }
                    });
                    return;
                }

                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(Objects.requireNonNull(getActivity()));
                bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog);
                SignInButton googleSignInButton = bottomSheetDialog.findViewById(R.id.google_sign_in);

                if (googleSignInButton != null)
                    googleSignInButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            bottomSheetDialog.cancel();
                            progressBar.setVisibility(View.VISIBLE);
                            Intent googleSignInIntent = googleSignInClient.getSignInIntent();
                            startActivityForResult(googleSignInIntent, GOOGLE_SIGN_IN_CODE);
                        }
                    });

                bottomSheetDialog.show();
            }
        });

        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            Glide.with(getActivity())
                    .load(Objects.requireNonNull(user.getPhotoUrl()).toString())
                    .into(circleImageView);
            name.setText(user.getDisplayName());
            email.setText(user.getEmail());
            tVLoginStatus.setText(getResources().getString(R.string.logout));
        } else {
            name.setVisibility(View.GONE);
            email.setVisibility(View.GONE);
            tVLoginStatus.setText(getResources().getString(R.string.login));
        }

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GOOGLE_SIGN_IN_CODE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null)
                    firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingsFragment())
                            .commit();
                } else {
                    Toast.makeText(getActivity(), "Login failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
