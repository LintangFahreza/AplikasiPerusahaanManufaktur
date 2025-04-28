/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PerusahaanManufaktur;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
/**
 *
 * @author LENOVO
 */
public class koneksi {
     public static Connection con;
    public static Statement stat;
    
    public static void koneksi(){
        try{
            String url = "jdbc:mysql://localhost:5000/db_perusahaan_manufaktur";
            String user = "root";
            String pw = "092004";
            con = DriverManager.getConnection(url, user, pw);
            stat = con.createStatement();
            System.out.println("Koneksi Berhasil Cuy");
        }catch(Exception e){
            System.err.println("Koneksi Gagal Cuy "+e.getMessage());
        }
    }
    
}
