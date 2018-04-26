package metodologia.etemb.castellan.ledcontroler;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Set;

/**
 * Criado por Henrique em 24/04/2018.
 */
public class ListaDispositivos extends ListActivity {
    static String ENDERECO_MAC;
    BluetoothAdapter meuBluetoothAdapter2 = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        meuBluetoothAdapter2 = BluetoothAdapter.getDefaultAdapter();

        ArrayAdapter<String> ArrayBluetooth = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

        Set<BluetoothDevice> dispPareados = meuBluetoothAdapter2.getBondedDevices();

        if (dispPareados.size() > 0) {
            for (BluetoothDevice dispositivo : dispPareados) {
                String nomeBT = dispositivo.getName();
                String MAC = dispositivo.getAddress();
                ArrayBluetooth.add(nomeBT + "\n" + MAC);
            }
        }
        setListAdapter(ArrayBluetooth);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        String infoG = ((TextView) v).getText().toString();

        String END_MAC = infoG.substring(infoG.length() - 17);

        //Toast.makeText(getApplicationContext(),"MAC: "+END_MAC,Toast.LENGTH_SHORT).show();

        Intent RETORNA_MAC = new Intent();
        RETORNA_MAC.putExtra(ENDERECO_MAC, END_MAC);
        setResult(RESULT_OK, RETORNA_MAC);
        finish();
    }
}