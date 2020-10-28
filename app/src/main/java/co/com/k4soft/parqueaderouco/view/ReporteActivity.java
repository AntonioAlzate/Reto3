package co.com.k4soft.parqueaderouco.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.amitshekhar.utils.DatabaseHelper;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.com.k4soft.parqueaderouco.R;
import co.com.k4soft.parqueaderouco.entidades.Movimiento;
import co.com.k4soft.parqueaderouco.persistencia.room.DataBaseHelper;

public class ReporteActivity extends AppCompatActivity {

    @BindView(R.id.txtTotalRecaudado)
    public TextView txtTotalRecaudado;
    @BindView(R.id.lstListaVehiculos)
    public ListView lstListaVehiculos;
    private DataBaseHelper db;

    private List<String> listaMovimientos;
    private ArrayAdapter<String> arrayAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reporte);
        db = DataBaseHelper.getDBMainThread(this);
        ButterKnife.bind(this);
        mostrarTotalRecaudado();
        cargarMovimientos();
    }

    private void cargarMovimientos(){
        List<Movimiento> listaMovimientosFinalizados = db.getMovimientoDAO().listarMovimientosFinalizados();

        String[] movimientosArray = new String[listaMovimientosFinalizados.size()];
        for (int i = 0; i < listaMovimientosFinalizados.size(); i++) {
            String placaFechas = String.format("  " + listaMovimientosFinalizados.get(i).getPlaca().toUpperCase() + "  |  " +
                    listaMovimientosFinalizados.get(i).getFechaEntrada() + "  |  " + listaMovimientosFinalizados.get(i).getFechaSalida());
            movimientosArray[i] = placaFechas;
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, movimientosArray);
        lstListaVehiculos.setAdapter(arrayAdapter);
    }

    private void mostrarTotalRecaudado(){
        List<Movimiento> listaMovimientosFinalizados = db.getMovimientoDAO().listarMovimientosFinalizados();
        double total = 0;

        if(listaMovimientosFinalizados.isEmpty()){
            txtTotalRecaudado.setText("0.0");
        }else {
            for(Movimiento movimiento : listaMovimientosFinalizados){
                total += movimiento.getPago();
            }
            txtTotalRecaudado.setText(String.format("%.2f", total));
        }
    }
}