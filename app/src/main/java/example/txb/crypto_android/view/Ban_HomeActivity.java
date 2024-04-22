package example.txb.crypto_android.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import example.txb.crypto_android.R;


public class Ban_HomeActivity extends AppCompatActivity {
    private ImageView btnCommand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ban_activity_home);

        btnCommand = findViewById(R.id.btnCommand);
        btnCommand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(Ban_HomeActivity.this,Ban_CommandActivity.class);
                startActivity(intent);
            }
        });
    }
}