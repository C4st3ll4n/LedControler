package metodologia.etemb.castellan.ledcontroler;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.media.MediaActionSound;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class TelaPrincipal extends AppCompatActivity {

    private static final int ATIVA_BLUETOOTH = 1;
    private static final int SOLICITA_CONEXAO = 2;

    int controle = 0;

    String led1 = "X";
    String led2 = "W";
    String led3 = "A";

    BluetoothAdapter meuBluetoothAdapter = null;
    BluetoothSocket meuSocket = null;
    BluetoothDevice meuDevice = null;

    MediaActionSound mas = new MediaActionSound();

    boolean conexão = false;

    ConnectedThread connectedThread;

    UUID MeuUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_principal);

        meuBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (meuBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "Bluetooth não encontrado!", Toast.LENGTH_SHORT).show();
        } else if (!meuBluetoothAdapter.isEnabled()) {
            mas.play(1);
            Intent ativarBt = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(ativarBt, ATIVA_BLUETOOTH);
        }


    }

    public void conectar(View v) {
        if (conexão) {
            try {
                meuSocket.close();
                conexão = false;
                Toast.makeText(TelaPrincipal.this, "Desconectado!", Toast.LENGTH_SHORT).show();
                mas.play(1);
            } catch (IOException e) {
                //Toast.makeText(MainActivity.this, "Erro ao desconectar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            //conectar
            mas.play(2);
            Intent abrirLista = new Intent(TelaPrincipal.this, ListaDispositivos.class);
            startActivityForResult(abrirLista, SOLICITA_CONEXAO);
        }
    }

    public void ctrlLED1(View v) {
        if (conexão) {
            connectedThread.enviar(led1);
            //Toast.makeText(MainActivity.this, "PARAR!", Toast.LENGTH_SHORT).show();
            mas.play(2);
        } else {

            Toast.makeText(TelaPrincipal.this, "Conecte-se antes", Toast.LENGTH_SHORT).show();
            mas.play(1);
        }
    }

    public void ctrlLED2(View v) {
        if (conexão) {
            connectedThread.enviar(led2);
            //Toast.makeText(MainActivity.this, "PARAR!", Toast.LENGTH_SHORT).show();
            mas.play(2);
        } else {
            Toast.makeText(TelaPrincipal.this, "Conecte-se antes", Toast.LENGTH_SHORT).show();
            mas.play(1);
        }
    }

    public void ctrlLED3(View v) {
        if (conexão) {
            connectedThread.enviar(led3);
            //Toast.makeText(MainActivity.this, "PARAR!", Toast.LENGTH_SHORT).show();
            mas.play(2);
        } else {
            Toast.makeText(TelaPrincipal.this, "Conecte-se antes", Toast.LENGTH_SHORT).show();
            mas.play(1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case ATIVA_BLUETOOTH:
                if (resultCode == Activity.RESULT_OK) {
                    //Toast.makeText(getApplicationContext(), "O Bluetooth foi ativado !", Toast.LENGTH_SHORT).show();
                    mas.play(2);
                } else {
                    mas.play(1);
                    Toast.makeText(getApplicationContext(), "O Bluetooth não foi ativado e o app foi fechado !", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;

            case SOLICITA_CONEXAO:
                if (resultCode == Activity.RESULT_OK) {
                    //noinspection ConstantConditions
                    String ENDEREÇO_MAC = data.getExtras().getString(ListaDispositivos.ENDERECO_MAC);
                    meuDevice = meuBluetoothAdapter.getRemoteDevice(ENDEREÇO_MAC);

                    try {
                        meuSocket = meuDevice.createRfcommSocketToServiceRecord(MeuUUID);

                        meuSocket.connect();

                        conexão = true;

                        connectedThread = new ConnectedThread(meuSocket);

                        connectedThread.start();

                        //Toast.makeText(getApplicationContext(), "CONECTADO COM: " + ENDEREÇO_MAC, Toast.LENGTH_SHORT).show();

                        mas.play(2);

                    } catch (IOException e) {
                        Toast.makeText(getApplicationContext(), "Erro ao tentar conectar: " + e, Toast.LENGTH_SHORT).show();
                        conexão = false;
                    }
                } else {
                    mas.play(1);
                    Toast.makeText(getApplicationContext(), "Algo de errado não está certo", Toast.LENGTH_SHORT).show();
                }
        }

    }

    private class ConnectedThread extends Thread {

        private final OutputStream mmOutStream;

        private ConnectedThread(BluetoothSocket socket) {
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                //Toast.makeText(MainActivity.this, "Erro no construtor: \n"+ e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            mmOutStream = tmpOut;
        }

        /* Call this from the main activity to send data to the remote device */
        private void enviar(String letra) {
            byte[] bytes = letra.getBytes();
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
                //Toast.makeText(MainActivity.this, "Erro ao tentar enviar dados: \n"+ e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

    }
}
