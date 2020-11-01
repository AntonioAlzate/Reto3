package co.com.k4soft.parqueaderouco.view;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.com.k4soft.parqueaderouco.R;
import co.com.k4soft.parqueaderouco.entidades.Movimiento;
import co.com.k4soft.parqueaderouco.persistencia.room.DataBaseHelper;
import co.com.k4soft.parqueaderouco.utilities.DateUtil;

public class ReporteActivity extends AppCompatActivity {

    @BindView(R.id.txtTotalRecaudado)
    public TextView txtTotalRecaudado;
    @BindView(R.id.lstListaVehiculos)
    public ListView lstListaVehiculos;
    @BindView(R.id.txtFechaInicial)
    public TextView txtFechaInicial;
    @BindView(R.id.txtFechaSalida)
    public TextView txtFechaSalida;

    private DataBaseHelper db;

    //private DatePickerDialog datePickerDialog;

    int diaActual = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    int mesActual = Calendar.getInstance().get(Calendar.MONTH);
    int yearActual = Calendar.getInstance().get(Calendar.YEAR);

    private List<String> listaMovimientos;
    private ArrayAdapter<String> arrayAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reporte);
        db = DataBaseHelper.getDBMainThread(this);
        ButterKnife.bind(this);
    }

    public void configurarFechaInicialDatePickerDialog(View view) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                String fecha = year + "-" + (month + 1) + "-" + dayOfMonth;
                Date fechaEnDate = DateUtil.convertStringToDateNotHour(fecha);
                txtFechaInicial.setText(DateUtil.convertDateToString(fechaEnDate));

                limpiarListViewAndTotalRecaudado();
            }
        }, yearActual, mesActual, diaActual);
        datePickerDialog.getDatePicker().setMaxDate(Calendar.getInstance().getTime().getTime());
        datePickerDialog.show();
    }

    public void configurarFechaFinalDatePickerDialog(View view) throws ParseException {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            int horasFinales = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            int minutosFinales = Calendar.getInstance().get(Calendar.MINUTE);
            int segundosFinales = Calendar.getInstance().get(Calendar.SECOND);
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                if(diaActual != dayOfMonth){
                    horasFinales = 23;
                    minutosFinales = 59;
                    segundosFinales = 59;
                }
                String hora = horasFinales + ":" + minutosFinales + ":" + segundosFinales;
                String fecha = year + "-" + (month + 1) + "-" + dayOfMonth + " " + hora;
                Date fechaEnDate = DateUtil.convertStringToDate(fecha);
                txtFechaSalida.setText(DateUtil.convertDateToString(fechaEnDate));

                limpiarListViewAndTotalRecaudado();
            }
        }, yearActual, mesActual, diaActual);
        datePickerDialog.getDatePicker().setMaxDate(Calendar.getInstance().getTime().getTime());
        datePickerDialog.show();
    }

    private void limpiarListViewAndTotalRecaudado(){
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item,new ArrayList<String>());
        lstListaVehiculos.setAdapter(arrayAdapter);
        txtTotalRecaudado.setText(getText(R.string.sin_configurar));
    }

    public void cargarMovimientos(View view) throws ParseException{

        if("".equals(txtFechaInicial.getText().toString()) || "".equals(txtFechaSalida.getText().toString())){
            Toast.makeText(getApplicationContext(), R.string.fechas_no_diligenciadas, Toast.LENGTH_LONG).show();
        }else{
            String fechaInicial = txtFechaInicial.getText().toString();
            String fechaSalida = txtFechaSalida.getText().toString();
            int diferenciaDias = DateUtil.timeFromDatesDias(fechaInicial, fechaSalida);
            if(diferenciaDias < 0) {
                Toast.makeText(getApplicationContext(), R.string.fecha_salida_no_valida, Toast.LENGTH_LONG).show();
            }else{
                List<Movimiento> listaMovimientosEnRango = db.getMovimientoDAO().listarMovimientosFinalizadosRango(fechaInicial, fechaSalida);
                mostrarTotalRecaudado(listaMovimientosEnRango);

                String[] movimientosArray = new String[listaMovimientosEnRango.size()];

                for (int i = 0; i < listaMovimientosEnRango.size(); i++) {
                    String placaFechas = String.format("  " + listaMovimientosEnRango.get(i).getPlaca().toUpperCase() + "  |  " +
                    listaMovimientosEnRango.get(i).getFechaEntrada() + "  |  " + listaMovimientosEnRango.get(i).getFechaSalida());
                    movimientosArray[i] = placaFechas;
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, movimientosArray);
                lstListaVehiculos.setAdapter(arrayAdapter);

                Toast.makeText(getApplicationContext(), R.string.reporte_generado_exitosamente, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void mostrarTotalRecaudado(List<Movimiento> movimientos){
        double total = 0;

        if(movimientos.isEmpty()){
            txtTotalRecaudado.setText("0.0");
        }else {
            for(Movimiento movimiento : movimientos){
                total += movimiento.getPago();
            }
            txtTotalRecaudado.setText(String.format("%.2f", total));
        }
    }
}