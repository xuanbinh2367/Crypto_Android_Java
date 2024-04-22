package example.txb.crypto_android.view;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.gson.Gson;

import example.txb.crypto_android.R;
import example.txb.crypto_android.model.SystemNotificationModel;
import example.txb.crypto_android.model.response.QuyProfileResponseModel;
import example.txb.crypto_android.view.adapter.BinhRankTopUserAdapter;
import example.txb.crypto_android.view_model.BaseViewModel;
import example.txb.crypto_android.view_model.BinhRankViewModel;


public class Binh_RankActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView txtRankPercent;
    Binh_ProfileActivity binhProfileActivity;
    private BinhRankViewModel rankViewModel;
    BinhRankTopUserAdapter binhRankTopUserAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.binh_activity_rank);

        rankViewModel = new BinhRankViewModel(getApplicationContext());

        //
        setObserve();
        ActionBar();
        initViews();
        // initData();

    }

    private void setObserve() {
        // Set alert error
        rankViewModel.notification().observe(this, new Observer<SystemNotificationModel>() {
            @Override
            public void onChanged(SystemNotificationModel it) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (it != null) {
                            Toast.makeText(Binh_RankActivity.this, it.content, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        // Set alert notification
    }


    private void loadData() {
        rankViewModel.getRank(1, 20, new BaseViewModel.OkCallback() {
            @Override
            public void handle(String data) {
                QuyProfileResponseModel.TopUsers parseOj = new Gson().fromJson(data, QuyProfileResponseModel.TopUsers.class);
                setData(parseOj);
            }
        });
    }

    private void setData(QuyProfileResponseModel.TopUsers data) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                binhRankTopUserAdapter.setList(data.items);
            }
        });

    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycle_list_rank);
        binhRankTopUserAdapter = new BinhRankTopUserAdapter(getApplicationContext());
        binhRankTopUserAdapter.setOpenCallback(new BinhRankTopUserAdapter.OpenCallback() {
            @Override
            public void open(String userId) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(Binh_RankActivity.this, Binh_ProfileActivity.class);
                        intent.putExtra("id", userId);
                        startActivity(intent);
                    }
                });
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(binhRankTopUserAdapter);

        txtRankPercent = findViewById(R.id.txt_rank_percent);
        Intent intent = getIntent();
        int betterPercent = intent.getIntExtra("betterPercent",0);
        txtRankPercent.setText(">" + betterPercent + "%");


        loadData();
    }

    public void ActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            // Đặt màu chữ
            int colorWhite = getResources().getColor(android.R.color.black);
            actionBar.setTitle(Html.fromHtml("<font color='" + colorWhite + "'>XẾP HẠNG</font>"));
            // Đặt màu của nút back
            Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.binh_ic_arrow_back);
            upArrow.setColorFilter(colorWhite, PorterDuff.Mode.SRC_ATOP);
            actionBar.setHomeAsUpIndicator(upArrow);
            // Đặt màu nền
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            setResult(RESULT_CANCELED);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}