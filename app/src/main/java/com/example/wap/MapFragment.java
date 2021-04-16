package com.example.wap;

import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.Layer;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.mapbox.mapboxsdk.style.layers.Property.NONE;
import static com.mapbox.mapboxsdk.style.layers.Property.VISIBLE;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.visibility;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment implements PermissionsListener, OnMapReadyCallback {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static final String DROPPED_MARKER_LAYER_ID = "DROPPED_MARKER_LAYER_ID";
    private MapView mapView;
    private MapboxMap mapboxMap;
    private FloatingActionButton selectLocationButton;
    private PermissionsManager permissionsManager;
    private ImageView hoveringMarker;
    private Layer droppedMarkerLayer;

    public MapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapFragment newInstance(String param1, String param2) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Mapbox access token is configured here. This needs to be called either in your application
        // object or in the same activity which contains the mapview.
        Mapbox.getInstance(getActivity(), String.valueOf(R.string.mapbox_access_token));

        // This contains the MapView in XML and needs to be called after the access token is configured.
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        selectLocationButton = getActivity().findViewById(R.id.fab);
        selectLocationButton.setImageResource(R.drawable.ic_baseline_add_24);

        // Initialize the Mapbox Map view
        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(getActivity(), R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted && mapboxMap != null) {
            Style style = mapboxMap.getStyle();
            if (style != null) {
                enableLocationPlugin(style);
            }
        } else {
            Toast.makeText(getActivity(), R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
            getActivity().finish();
        }
    }

    @SuppressWarnings({"MissingPermission"})
    private void enableLocationPlugin(@NonNull Style loadedMapStyle) {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(getActivity())) {

            // Get an instance of the component. Adding in LocationComponentOptions is also an optional
            // parameter
            LocationComponent locationComponent = mapboxMap.getLocationComponent();
            locationComponent.activateLocationComponent(LocationComponentActivationOptions.builder(
                    getActivity(), loadedMapStyle).build());
            locationComponent.setLocationComponentEnabled(true);

            // Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);
            locationComponent.setRenderMode(RenderMode.NORMAL);

        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(getActivity());
        }
    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        MapFragment.this.mapboxMap = mapboxMap;
        mapboxMap.setStyle(Style.MAPBOX_STREETS, style -> {
            enableLocationPlugin(style);

            // When user is still picking a location, we hover a marker above the mapboxMap in the center.
            // This is done by using an image view with the default marker found in the SDK. You can
            // swap out for your own marker image, just make sure it matches up with the dropped marker.
            hoveringMarker = new ImageView(getActivity());
            hoveringMarker.setImageResource(R.drawable.red_marker);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
            hoveringMarker.setLayoutParams(params);
            mapView.addView(hoveringMarker);

            // Initialize, but don't show, a SymbolLayer for the marker icon which will represent a selected location.
            initDroppedMarker(style);

            // Button for user to drop marker or to pick marker back up.
            selectLocationButton.setOnClickListener(view -> {
                if (hoveringMarker.getVisibility() == View.VISIBLE) {

                    // Use the map target's coordinates to make a reverse geocoding search
                    final LatLng mapTargetLatLng = mapboxMap.getCameraPosition().target;

                    // Hide the hovering red hovering ImageView marker
                    hoveringMarker.setVisibility(View.INVISIBLE);
                    // Transform the appearance of the button to become the cancel button
                    selectLocationButton.setBackgroundColor(
                            ContextCompat.getColor(getActivity(), R.color.colorAccent));

                    // Show the SymbolLayer icon to represent the selected map location
                    if (style.getLayer(DROPPED_MARKER_LAYER_ID) != null) {
                        GeoJsonSource source = style.getSourceAs("dropped-marker-source-id");
                        if (source != null) {
                            source.setGeoJson(Point.fromLngLat(mapTargetLatLng.getLongitude(), mapTargetLatLng.getLatitude()));

                            // Create bbox from single point. Adding .015 to the value.
                            double lng = mapTargetLatLng.getLongitude();
                            double lat = mapTargetLatLng.getLatitude();

                            //Value modifiers.
                            double northEastMod = .10;
                            double southWestMod = -.10;

                            //Generated 4 points of bbox
                            double north = lat + northEastMod;
                            double east = lng + northEastMod;
                            double south = lng + southWestMod;
                            double west = lat + southWestMod;

                            String bbox = north + "," + east + "," + west + "," + south;

                            Log.d("MH", north + "," + east + "," + west + "," + south);
                            Log.d("MH", "Lng: " + mapTargetLatLng.getLongitude() + " Lat: " + mapTargetLatLng.getLatitude());

                            // Add image url to local database.
                            AppDatabase db = Room.databaseBuilder(getActivity(),
                                    AppDatabase.class, "image-links").fallbackToDestructiveMigration().allowMainThreadQueries().build();

                            ImageLinkDao imageLinkDao = db.imageLinkDao();
                            String url = volleySentinelRequest(bbox);

                            ImageLink imageLink = new ImageLink(url);
                            imageLinkDao.addImageLink(imageLink);

                        }
                        droppedMarkerLayer = style.getLayer(DROPPED_MARKER_LAYER_ID);
                        if (droppedMarkerLayer != null) {
                            droppedMarkerLayer.setProperties(visibility(VISIBLE));
                        }

                        selectLocationButton.setImageResource(R.drawable.ic_baseline_horizontal_rule_24);
                    }

                    // Use the map camera target's coordinates to make a reverse geocoding search
//                            reverseGeocode(Point.fromLngLat(mapTargetLatLng.getLongitude(), mapTargetLatLng.getLatitude()));

                } else {

                    // Switch the button appearance back to select a location.
                    selectLocationButton.setBackgroundColor(
                            ContextCompat.getColor(getActivity(), R.color.colorPrimary));
                    // Lets get the plus on the FAB to a minus here
                    selectLocationButton.setImageResource(R.drawable.ic_baseline_add_24);

                    // Show the red hovering ImageView marker
                    hoveringMarker.setVisibility(View.VISIBLE);

                    // Hide the selected location SymbolLayer
                    droppedMarkerLayer = style.getLayer(DROPPED_MARKER_LAYER_ID);
                    if (droppedMarkerLayer != null) {
                        droppedMarkerLayer.setProperties(visibility(NONE));
                    }
                }
            });
        });
    }

    private void initDroppedMarker(@NonNull Style loadedMapStyle) {
        // Add the marker image to map
        loadedMapStyle.addImage("dropped-icon-image", BitmapFactory.decodeResource(
                getResources(), R.drawable.blue_marker));
        loadedMapStyle.addSource(new GeoJsonSource("dropped-marker-source-id"));
        loadedMapStyle.addLayer(new SymbolLayer(DROPPED_MARKER_LAYER_ID,
                "dropped-marker-source-id").withProperties(
                iconImage("dropped-icon-image"),
                visibility(NONE),
                iconAllowOverlap(true),
                iconIgnorePlacement(true)
        ));
    }

    // Perform Sentinel API Request
    public String volleySentinelRequest(String bbox) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Boolean isHighRes, isArgi;

        isArgi = prefs.getBoolean("image_layer", false);
        isHighRes = prefs.getBoolean("image_res", false);

        String returnURL;

        String layer = "TRUE_COLOR";
        String maxCC = "50";
        String format = "image/jpeg";
        String CRS = "EPSG:4326";

        // Picture Width and Height.
        String width;
        String height;

        String lowRes = "320";
        String highRes = "640";

        width = lowRes;
        height = lowRes;

        // Settings check for resolution.
        if (isHighRes) {
            width = highRes;
            height = highRes;
        }
        if(isArgi){
            layer = "AGRICULTURE";
        }

        // Date range.
        String startDate = "2018-03-29";

        // End date of todays date.
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");//dd/MM/yyyy
        Date now = new Date();
        String endDate = sdfDate.format(now);

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = "https://services.sentinel-hub.com/ogc/wms/f7db87a9-d00e-41de-a2af-d618d367eed8?REQUEST=GetMap&BBOX=" +
                bbox +
                "&LAYERS=" + layer +
                "&MAXCC=" + maxCC +
                "&WIDTH=" + width + "&HEIGHT=" + height +
                "&FORMAT=" + format +
                "&TIME=" + startDate + "/" + endDate +
                "&CRS=" + CRS;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {

                    // Option to manipulate response
                }, error -> {

            // Option to display error message
        });

        queue.add(stringRequest);

        return url;
    }
}