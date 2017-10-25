package prashushi.stepsfordrivers;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.select_bus).setOnClickListener(this);
        findViewById(R.id.enter_driver).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.select_bus:
                startActivity(new Intent(this, ListBusActivity.class));
                break;
            case R.id.enter_driver:
                startActivity(new Intent(this, LoginDriver.class));
                break;

        }
    }
}
