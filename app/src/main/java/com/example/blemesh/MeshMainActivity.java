package com.example.blemesh;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.airoha.libmesh.listener.MeshConfigurationModelListener;
import com.airoha.libmesh.listener.MeshProvisionListener;
import com.airoha.libmeshparam.model.config.config_client_evt_appkey_list_t;
import com.airoha.libmeshparam.model.config.config_client_evt_appkey_status_t;
import com.airoha.libmeshparam.model.config.config_client_evt_beacon_status_t;
import com.airoha.libmeshparam.model.config.config_client_evt_composition_data_status_t;
import com.airoha.libmeshparam.model.config.config_client_evt_default_ttl_status_t;
import com.airoha.libmeshparam.model.config.config_client_evt_friend_status_t;
import com.airoha.libmeshparam.model.config.config_client_evt_gatt_proxy_status_t;
import com.airoha.libmeshparam.model.config.config_client_evt_heartbeat_publication_status_t;
import com.airoha.libmeshparam.model.config.config_client_evt_heartbeat_subscription_status_t;
import com.airoha.libmeshparam.model.config.config_client_evt_key_refresh_phase_status_t;
import com.airoha.libmeshparam.model.config.config_client_evt_lpn_poll_timeout_status_t;
import com.airoha.libmeshparam.model.config.config_client_evt_model_app_list_t;
import com.airoha.libmeshparam.model.config.config_client_evt_model_app_status_t;
import com.airoha.libmeshparam.model.config.config_client_evt_model_publication_status_t;
import com.airoha.libmeshparam.model.config.config_client_evt_model_subscription_list_t;
import com.airoha.libmeshparam.model.config.config_client_evt_model_subscription_status_t;
import com.airoha.libmeshparam.model.config.config_client_evt_netkey_list_t;
import com.airoha.libmeshparam.model.config.config_client_evt_netkey_status_t;
import com.airoha.libmeshparam.model.config.config_client_evt_network_transmit_status_t;
import com.airoha.libmeshparam.model.config.config_client_evt_node_identity_status_t;
import com.airoha.libmeshparam.model.config.config_client_evt_relay_status_t;
import com.airoha.libmeshparam.prov.ble_mesh_evt_prov_ali_confirmation_device;
import com.airoha.libmeshparam.prov.ble_mesh_evt_prov_ali_response;
import com.airoha.libmeshparam.prov.ble_mesh_prov_capabilities_t;

public class MeshMainActivity extends BaseActivity implements MeshConfigurationModelListener, MeshProvisionListener {
    private final static String TAG = "Airoha_" + MeshMainActivity.class.getSimpleName();

    private MeshMainActivity mContext;
    private static final int REQUEST_ENABLE_GPS = 2;
    private static final int REQUEST_PERMISSION_RETURN_CODE = 5566;

    private Button mBtnNetwork;
    private Button mBtnScan;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.mesh_main_activity);
    }

    @Override
    public void onMeshConfigCompositionReceived(config_client_evt_composition_data_status_t configComposition) {

    }

    @Override
    public void onMeshConfigNetKeyStatusReceived(config_client_evt_netkey_status_t configNetkeyStatus) {

    }

    @Override
    public void onMeshConfigNetKeyListReceived(config_client_evt_netkey_list_t configNetkeyList) {

    }

    @Override
    public void onMeshConfigAppKeyStatusReceived(config_client_evt_appkey_status_t configAppkeyStatus) {

    }

    @Override
    public void onMeshConfigAppKeyListReceived(config_client_evt_appkey_list_t configAppkeyList) {

    }

    @Override
    public void onMeshConfigModelAppStatusReceived(config_client_evt_model_app_status_t configModelAppStatus) {

    }

    @Override
    public void onMeshConfigModelAppListReceived(config_client_evt_model_app_list_t configModelAppList) {

    }

    @Override
    public void onMeshConfigSubscriptionStatusReceived(config_client_evt_model_subscription_status_t subscriptionStatus) {

    }

    @Override
    public void onMeshConfigPublicationStatusReceived(config_client_evt_model_publication_status_t publicationStatus) {

    }

    @Override
    public void onMeshConfigBeaconStatusReceived(config_client_evt_beacon_status_t beaconStatus) {

    }

    @Override
    public void onMeshConfigDefaultTtlStatusReceived(config_client_evt_default_ttl_status_t defaultTtlStatus) {

    }

    @Override
    public void onMeshConfigGattPorxyStatusReceived(config_client_evt_gatt_proxy_status_t gattPorxyStatus) {

    }

    @Override
    public void onMeshConfigKeyRefreshPhaseStatusReceived(config_client_evt_key_refresh_phase_status_t keyRefreshPhaseStatus) {

    }

    @Override
    public void onMeshConfigFriendStatusReceived(config_client_evt_friend_status_t friendStatus) {

    }

    @Override
    public void onMeshConfigRelayStatusReceived(config_client_evt_relay_status_t relayStatus) {

    }

    @Override
    public void onMeshConfigModelSubscriptionListReceived(config_client_evt_model_subscription_list_t modelSubscriptionList) {

    }

    @Override
    public void onMeshConfigNodeIdentityStatusReceived(config_client_evt_node_identity_status_t nodeIdentityStatus) {

    }

    @Override
    public void onMeshConfigHeartbeatPublicationStatusReceived(config_client_evt_heartbeat_publication_status_t heartbeatPublicationStatus) {

    }

    @Override
    public void onMeshConfigHeartbeatSubscriptionStatusReceived(config_client_evt_heartbeat_subscription_status_t heartbeatSubscriptionStatus) {

    }

    @Override
    public void onMeshConfigNetworkTransmitStatusReceived(config_client_evt_network_transmit_status_t networkTransmitStatus) {

    }

    @Override
    public void onMeshConfigLpnPollTimeoutStatusReceived(config_client_evt_lpn_poll_timeout_status_t lpnPollTimeoutStatus) {

    }

    @Override
    public void onMeshConfigResetNodeStatusReceived(int status) {

    }

    @Override
    public void onMeshUdFound(BluetoothDevice device, int rssi, byte[] uuid, short oobInfo, byte[] uriHash) {

    }

    @Override
    public void onMeshProvCapReceived(ble_mesh_prov_capabilities_t provCap) {

    }

    @Override
    public void onMeshProvStateChanged(boolean state, byte[] deviceKey, short address) {

    }

    @Override
    public void onMeshAliProvisioningResponse(ble_mesh_evt_prov_ali_response provAliResp) {

    }

    @Override
    public void onMeshAliProvisioningConfirmationDevice(ble_mesh_evt_prov_ali_confirmation_device provAliConfirmDevice) {

    }
}
