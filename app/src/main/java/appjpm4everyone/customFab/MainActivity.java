package appjpm4everyone.customFab;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ContentFrameLayout;

import appjpm4everyone.jpmfab.CommunicateFab;
import appjpm4everyone.jpmfab.MovableFloatingActionButton;

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
        int spaceFab = (int) getResources().getDimension(R.dimen.fab_params);
        final MovableFloatingActionButton movableFloatingActionButton = new MovableFloatingActionButton(context, true, spaceFab);
        ContentFrameLayout rootLayout = findViewById(android.R.id.content);
        if (rootLayout.findViewById(R.id.layout_fab) == null) {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(spaceFab, spaceFab);

            movableFloatingActionButton.setResources(R.drawable.radioactive_free, ImageView.ScaleType.CENTER,
                    ColorStateList.valueOf(Color.TRANSPARENT), 1.0f);

            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            params.addRule(RelativeLayout.ALIGN_PARENT_END);

            rootLayout.addView(movableFloatingActionButton, params);


        }
        movableFloatingActionButton.show(!isNavTransparent());
        rootLayout.bringChildToFront(movableFloatingActionButton);
        movableFloatingActionButton.requestFocus();

    }


    public boolean isNavTransparent() {
        return false;
    }

    @Override
    public void onClickFab(float xPos, float yPos) {
        Toast.makeText(this, "Xpos : " + xPos + " Ypos : " + yPos, Toast.LENGTH_SHORT).show();
    }

}
