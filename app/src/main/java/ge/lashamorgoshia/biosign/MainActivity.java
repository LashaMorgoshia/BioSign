package ge.lashamorgoshia.biosign;

import android.graphics.Bitmap;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    DrawingView dv ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<ActionInfo> actions = dv.Actions;
                Bitmap bmp = dv.mBitmap;

                String content = "";
                for (int i = 0; i < actions.size(); i++ ) {
                    ActionInfo a = actions.get(i);
                    content += a.Name + "|Pressure:" + a.Pressure + "|NanoTime:" + a.NanoTime + "|X:" + a.X + "|XVelocity:" + a.XVelocity + "|Y:" + a.Y + "|YVelocity:" + a.YVelocity + "|TouchSize:" + a.TouchSize + ".\r\n";
                }

                ((TextView)findViewById(R.id.txtContent)).setText("");
                ((TextView)findViewById(R.id.txtContent)).setText(content);

                findViewById(R.id.fabClean).setVisibility(View.VISIBLE);
            }
        });

        FloatingActionButton fabClean = findViewById(R.id.fabClean);
        fabClean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dv.Actions.clear();
                ((TextView)findViewById(R.id.txtContent)).setText("");
                findViewById(R.id.fabClean).setVisibility(View.GONE);
            }
        });

        dv = new DrawingView(this);
        LinearLayout signatureLayout = findViewById(R.id.signatureLayout);
        signatureLayout.addView(dv);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}