package appjpm4everyone.customFab;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import appjpm4everyone.libraryFAB.CommunicateFab;
import appjpm4everyone.libraryFAB.MovableFloatingActionButton;

public class MainActivity extends AppCompatActivity implements CommunicateFab {

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        showExtendFab();
    }

    private void showExtendFab() {
        MovableFloatingActionButton movableFloatingActionButton = new MovableFloatingActionButton(context, true, null);
        movableFloatingActionButton.openFAB(this,  null, null, null, null);
    }

    @Override
    public void onClickFab(float xPos, float yPos) {
        Toast.makeText(this, "Xpos : " + xPos + " Ypos : " + yPos, Toast.LENGTH_SHORT).show();
    }

}
