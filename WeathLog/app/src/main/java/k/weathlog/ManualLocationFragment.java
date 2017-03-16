package k.weathlog;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;


public class ManualLocationFragment extends Fragment {

    private OnManualLocationListener listener;

    public ManualLocationFragment() {
        // Required empty public constructor
    }

    public static ManualLocationFragment newInstance() {
        ManualLocationFragment fragment = new ManualLocationFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_manual_location, container, false);

        final EditText latText = (EditText) view.findViewById(R.id.lat_text);
        final EditText lonText = (EditText) view.findViewById(R.id.lon_text);

        ImageView button = (ImageView) view.findViewById(R.id.accept_location_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Location location = new Location("");

                double lat = Double.parseDouble(latText.getText().toString());
                double lon = Double.parseDouble(lonText.getText().toString());

                location.setLatitude(lat);
                location.setLongitude(lon);

                Log.i("a", Double.toString(location.getLatitude()));
                Log.i("a", Double.toString(location.getLongitude()));


                if (listener != null) {
                    listener.onManualLocationProvided(location);
                }
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);
        if (context instanceof OnManualLocationListener) {
            listener = (OnManualLocationListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnManualLocationListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface OnManualLocationListener {
        void onManualLocationProvided(Location location);
    }
}
