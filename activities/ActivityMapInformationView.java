//package com.skeds.android.phone.business.Activities.Phone;
//
//import java.util.List;
//
//import android.content.Context;
//import android.location.Criteria;
//import android.location.Location;
//import android.location.LocationListener;
//import android.location.LocationManager;
//import android.os.Bundle;
//import android.widget.TextView;
//
//import com.google.android.maps.GeoPoint;
//import com.google.android.maps.MapController;
//import com.google.android.maps.MapView;
//import com.google.android.maps.Overlay;
//import com.google.android.maps.Projection;
//import com.skeds.android.phone.business.R;
//import com.skeds.android.phone.business.Activities.BaseSkedsActivity;
//import com.skeds.android.phone.business.Utilities.General.AppSettingsUtilities;
//
//public class ActivityMapInformationView extends BaseSkedsActivity implements
//		LocationListener {
//
//	public static com.skeds.android.phone.business.Utilities.General.ClassObjects.Location mapLocation = new com.skeds.android.phone.business.Utilities.General.ClassObjects.Location();
//
//	private List<GeoPoint> geoPoints;
//
//	private MapController mapController;
//	private MapController mc;
//	private String addressString = "No address found";
//	private List<Overlay> mapOverlays;
//	private Projection projection;
//	private Overlay locationOverlays;
//
//	private MapView mapView;
//	private TextView address1, address2, latitudeText, longitudeText;
//
//	private GeoPoint myLocation, myDestination;
//	private LocationManager locationManager;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		// TODO Auto-generated method stub
//		super.onCreate(savedInstanceState);
//
//		setContentView(R.layout.layout_map_information_view);
//		mapView = (MapView) findViewById(R.id.mapInformationMapView);
//
//		address1 = (TextView) findViewById(R.id.mapInformationTextAddress1);
//		address2 = (TextView) findViewById(R.id.mapInformationTextAddress2);
//		latitudeText = (TextView) findViewById(R.id.mapInformationTextLatitude);
//		longitudeText = (TextView) findViewById(R.id.mapInformationTextLongitude);
//
//		setupUI();
//	}
//
//
//
//	private void setupUI() {
//
//		mapView.setBuiltInZoomControls(false);
//		mapOverlays = mapView.getOverlays();
//		projection = mapView.getProjection();
//
//		mapController = mapView.getController();
//
//		// Configure the map display options
//		mapView.setSatellite(true);
//		mapView.setStreetView(true);
//
//		// Zoom in
//		mapController.setZoom(17);
//
//		mapView.setBuiltInZoomControls(true);
//
//		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//
//		Criteria criteria = new Criteria();
//		criteria.setAccuracy(Criteria.ACCURACY_FINE);
//		criteria.setAltitudeRequired(false);
//		criteria.setBearingRequired(false);
//		criteria.setCostAllowed(true);
//		criteria.setPowerRequirement(Criteria.POWER_LOW);
//		String provider = locationManager.getBestProvider(criteria, true);
//
//		Location location = locationManager.getLastKnownLocation(provider);
//
//		if (AppSettingsUtilities.isUseGPS()) {
//			locationManager.requestLocationUpdates(
//					LocationManager.GPS_PROVIDER, 300000, 5000, this);
//		}
//
//		mc = mapView.getController();
//		double lat = Double.parseDouble(mapLocation.getLatitude());
//		double lng = Double.parseDouble(mapLocation.getLongitude());
//
//		myDestination = new GeoPoint((int) (lat * 1E6), (int) (lng * 1E6));
//
//	}
//
//	@Override
//	public void onLocationChanged(Location location) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void onProviderDisabled(String provider) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void onProviderEnabled(String provider) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void onStatusChanged(String provider, int status, Bundle extras) {
//		// TODO Auto-generated method stub
//
//	}
//}