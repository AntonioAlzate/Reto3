package co.com.k4soft.parqueaderouco.persistencia.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import co.com.k4soft.parqueaderouco.entidades.Movimiento;
import co.com.k4soft.parqueaderouco.entidades.Tarifa;

@Dao
public interface MovimientoDAO {

    @Query("SELECT * FROM movimiento Where placa=:placa AND finalizaMovimiento = 0")
    Movimiento  findByPLaca(String placa);

    @Query("SELECT * FROM movimiento")
    List<Movimiento> listar();

    @Query("SELECT * FROM movimiento where finalizaMovimiento = 1 ORDER BY fechaSalida")
    List<Movimiento> listarMovimientosFinalizados();

    @Update
    void update(Movimiento movimiento);

    @Insert
    void insert(Movimiento movimiento);
}
