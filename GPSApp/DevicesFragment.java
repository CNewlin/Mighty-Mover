package com.example.gpsapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;
import com.example.gpsapp.BluetoothUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/* loaded from: classes3.dex */
public class DevicesFragment extends ListFragment {
    private BluetoothAdapter bluetoothAdapter;
    private ArrayAdapter<BluetoothDevice> listAdapter;
    private final ArrayList<BluetoothDevice> listItems = new ArrayList<>();
    private Menu menu;
    private boolean permissionMissing;
    ActivityResultLauncher<String> requestBluetoothPermissionLauncherForRefresh;

    @Override // androidx.fragment.app.Fragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getActivity().getPackageManager().hasSystemFeature("android.hardware.bluetooth")) {
            this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        this.listAdapter = new ArrayAdapter<BluetoothDevice>(getActivity(), 0, this.listItems) { // from class: com.example.gpsapp.DevicesFragment.1
            @Override // android.widget.ArrayAdapter, android.widget.Adapter
            public View getView(int position, View view, ViewGroup parent) {
                BluetoothDevice device = (BluetoothDevice) DevicesFragment.this.listItems.get(position);
                if (view == null) {
                    view = DevicesFragment.this.getActivity().getLayoutInflater().inflate(R.layout.device_list_item, parent, false);
                }
                TextView text1 = (TextView) view.findViewById(R.id.text1);
                TextView text2 = (TextView) view.findViewById(R.id.text2);
                String deviceName = device.getName();
                text1.setText(deviceName);
                text2.setText(device.getAddress());
                return view;
            }
        };
        this.requestBluetoothPermissionLauncherForRefresh = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback() { // from class: com.example.gpsapp.DevicesFragment$$ExternalSyntheticLambda1
            @Override // androidx.activity.result.ActivityResultCallback
            public final void onActivityResult(Object obj) {
                DevicesFragment.this.m44lambda$onCreate$0$comexamplegpsappDevicesFragment((Boolean) obj);
            }
        });
    }

    /* renamed from: lambda$onCreate$0$com-example-gpsapp-DevicesFragment, reason: not valid java name */
    /* synthetic */ void m44lambda$onCreate$0$comexamplegpsappDevicesFragment(Boolean granted) {
        BluetoothUtil.onPermissionsResult(this, granted.booleanValue(), new BluetoothUtil.PermissionGrantedCallback() { // from class: com.example.gpsapp.DevicesFragment$$ExternalSyntheticLambda2
            @Override // com.example.gpsapp.BluetoothUtil.PermissionGrantedCallback
            public final void call() {
                DevicesFragment.this.refresh();
            }
        });
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setListAdapter(null);
        View header = getActivity().getLayoutInflater().inflate(R.layout.device_list_header, (ViewGroup) null, false);
        getListView().addHeaderView(header, null, false);
        setEmptyText("initializing...");
        ((TextView) getListView().getEmptyView()).setTextSize(18.0f);
        setListAdapter(this.listAdapter);
    }

    @Override // androidx.fragment.app.Fragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        this.menu = menu;
        inflater.inflate(R.menu.menu_devices, menu);
        if (this.permissionMissing) {
            menu.findItem(R.id.bt_refresh).setVisible(true);
        }
        if (this.bluetoothAdapter == null) {
            menu.findItem(R.id.bt_settings).setEnabled(false);
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        refresh();
    }

    @Override // androidx.fragment.app.Fragment
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.bt_settings) {
            Intent intent = new Intent();
            intent.setAction("android.settings.BLUETOOTH_SETTINGS");
            startActivity(intent);
            return true;
        }
        if (id == R.id.bt_refresh) {
            if (BluetoothUtil.hasPermissions(this, this.requestBluetoothPermissionLauncherForRefresh)) {
                refresh();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void refresh() {
        this.listItems.clear();
        if (this.bluetoothAdapter != null) {
            if (Build.VERSION.SDK_INT >= 31) {
                this.permissionMissing = getActivity().checkSelfPermission("android.permission.BLUETOOTH_CONNECT") != 0;
                Menu menu = this.menu;
                if (menu != null && menu.findItem(R.id.bt_refresh) != null) {
                    this.menu.findItem(R.id.bt_refresh).setVisible(this.permissionMissing);
                }
            }
            if (!this.permissionMissing) {
                for (BluetoothDevice device : this.bluetoothAdapter.getBondedDevices()) {
                    if (device.getType() != 2) {
                        this.listItems.add(device);
                    }
                }
                Collections.sort(this.listItems, new Comparator() { // from class: com.example.gpsapp.DevicesFragment$$ExternalSyntheticLambda0
                    @Override // java.util.Comparator
                    public final int compare(Object obj, Object obj2) {
                        return BluetoothUtil.compareTo((BluetoothDevice) obj, (BluetoothDevice) obj2);
                    }
                });
            }
        }
        BluetoothAdapter bluetoothAdapter = this.bluetoothAdapter;
        if (bluetoothAdapter == null) {
            setEmptyText("<bluetooth not supported>");
        } else if (!bluetoothAdapter.isEnabled()) {
            setEmptyText("<bluetooth is disabled>");
        } else if (this.permissionMissing) {
            setEmptyText("<permission missing, use REFRESH>");
        } else {
            setEmptyText("<no bluetooth devices found>");
        }
        this.listAdapter.notifyDataSetChanged();
    }

    @Override // androidx.fragment.app.ListFragment
    public void onListItemClick(ListView l, View v, int position, long id) {
        BluetoothDevice device = this.listItems.get(position - 1);
        Bundle args = new Bundle();
        args.putString("device", device.getAddress());
        Fragment fragment = new TerminalFragment();
        fragment.setArguments(args);
        getParentFragmentManager().beginTransaction().replace(R.id.fragment, fragment, "terminal").addToBackStack(null).commit();
    }
}