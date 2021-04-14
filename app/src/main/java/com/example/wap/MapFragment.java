package com.example.wap;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;


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

import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;


import org.jetbrains.annotations.NotNull;

import static com.mapbox.mapboxsdk.style.layers.Property.NONE;
import static com.mapbox.mapboxsdk.style.layers.Property.VISIBLE;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.visibility;
    /**
     * Drop a marker at a specific location and then perform
     * reverse geocoding to retrieve and display the location's address
     */
    public class MapFragment extends Fragment implements PermissionsListener, OnMapReadyCallback {

        private static final String DROPPED_MARKER_LAYER_ID = "DROPPED_MARKER_LAYER_ID";
        private MapView mapView;
        private MapboxMap mapboxMap;
        private Button selectLocationButton;
        private PermissionsManager permissionsManager;
        private ImageView hoveringMarker;
        private Layer droppedMarkerLayer;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            // Mapbox access token is configured here. This needs to be called either in your application
            // object or in the same activity which contains the mapview.
            Mapbox.getInstance(getActivity(), "pk.eyJ1IjoibnN0YWxnaWMiLCJhIjoiY2trZndsZWV1MDlnYTJ2azJmOHlkdzZldiJ9.3Y1Gl-AFgXzgRSCYLhRMdA");

            View view = inflater.inflate(R.layout.fragment_map, container, false);
            // This contains the MapView in XML and needs to be called after the access token is configured.
            getActivity().setContentView(R.layout.activity_lab_location_picker);

            // Initialize the mapboxMap view
            mapView = getActivity().findViewById(R.id.mapView);
            mapView.onCreate(savedInstanceState);
            mapView.getMapAsync(this);

            return view;
        }

        @Override
        public void onMapReady(@NonNull final MapboxMap mapboxMap) {
            com.example.wap.MapFragment.this.mapboxMap = mapboxMap;
            mapboxMap.setStyle(Style.MAPBOX_STREETS, style -> {
                enableLocationPlugin(style);

                // Variable never used, commenting out for future exploration
                // CameraPosition position = new CameraPosition.Builder().zoom(10).build();

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
                selectLocationButton = requireActivity().findViewById(R.id.select_location_button);
                selectLocationButton.setOnClickListener(view -> {
                    if (hoveringMarker.getVisibility() == View.VISIBLE) {

                        // Use the map target's coordinates to make a reverse geocoding search
                        final LatLng mapTargetLatLng = mapboxMap.getCameraPosition().target;

                        // Hide the hovering red hovering ImageView marker
                        hoveringMarker.setVisibility(View.INVISIBLE);
                        // Transform the appearance of the button to become the cancel button
                        selectLocationButton.setBackgroundColor(
                                ContextCompat.getColor(requireActivity(), R.color.colorAccent));
                        selectLocationButton.setText(getString(R.string.location_picker_select_location_button_cancel));

                        // Show the SymbolLayer icon to represent the selected map location
                        if (style.getLayer(DROPPED_MARKER_LAYER_ID) != null) {
                            GeoJsonSource source = style.getSourceAs("dropped-marker-source-id");
                            if (source != null) {
                                source.setGeoJson(Point.fromLngLat(mapTargetLatLng.getLongitude(), mapTargetLatLng.getLatitude()));

                                // Create bbox from single point. Adding .015 to the value.
                                double lng = mapTargetLatLng.getLongitude();
                                double lat = mapTargetLatLng.getLatitude();

                                //Value modifiers.
                                double northEastMod = .15;
                                double southWestMod = -.15;

                                //Generated 4 points of bbox
                                double north = lat + northEastMod;
                                double east =  lng + northEastMod;
                                double south = lng + southWestMod;
                                double west = lat + southWestMod;

                                Log.d("MH", north + "," + east + "," + west + "," + south);
                                Log.d("MH","Lng: " + mapTargetLatLng.getLongitude() + " Lat: " + mapTargetLatLng.getLatitude() );

                                Toast.makeText(getActivity(), north + "," + east + "," + west + "," + south , Toast.LENGTH_SHORT).show();
                            }
                            droppedMarkerLayer = style.getLayer(DROPPED_MARKER_LAYER_ID);
                            if (droppedMarkerLayer != null) {
                                droppedMarkerLayer.setProperties(visibility(VISIBLE));
                            }
                        }

                        // Use the map camera target's coordinates to make a reverse geocoding search
//                            reverseGeocode(Point.fromLngLat(mapTargetLatLng.getLongitude(), mapTargetLatLng.getLatitude()));

                    } else {

                        // Switch the button appearance back to select a location.
                        selectLocationButton.setBackgroundColor(
                                ContextCompat.getColor(requireActivity(), R.color.colorPrimary));
                        selectLocationButton.setText(getString(R.string.location_picker_select_location_button_select));

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

        @Override
        public void onResume() {
            super.onResume();
            mapView.onResume();
        }

        @Override
        @SuppressWarnings( {"MissingPermission"})
        public void onStart() {
            super.onStart();
            mapView.onStart();
        }

        @Override
        public void onStop() {
            super.onStop();

            mapView.onStop();
        }

        @Override
        public void onPause() {
            super.onPause();
            mapView.onPause();
        }

        @Override
        public void onSaveInstanceState(@NotNull Bundle outState) {
            super.onSaveInstanceState(outState);
            mapView.onSaveInstanceState(outState);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            mapView.onDestroy();
        }

        @Override
        public void onLowMemory() {
            super.onLowMemory();
            mapView.onLowMemory();
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
                requireActivity().finish();
            }
        }


        @SuppressWarnings( {"MissingPermission"})
        private void enableLocationPlugin(@NonNull Style loadedMapStyle) {
            // Check if permissions are enabled and if not request
            if (PermissionsManager.areLocationPermissionsGranted(requireActivity())) {

                // Get an instance of the component. Adding in LocationComponentOptions is also an optional
                // parameter
                LocationComponent locationComponent = mapboxMap.getLocationComponent();
                locationComponent.activateLocationComponent(LocationComponentActivationOptions.builder(
                        requireActivity(), loadedMapStyle).build());
                locationComponent.setLocationComponentEnabled(true);

                // Set the component's camera mode
                locationComponent.setCameraMode(CameraMode.TRACKING);
                locationComponent.setRenderMode(RenderMode.NORMAL);

            } else {
                permissionsManager = new PermissionsManager(this);
                permissionsManager.requestLocationPermissions(getActivity());
            }
        }
    }
