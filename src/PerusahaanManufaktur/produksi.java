package PerusahaanManufaktur;

import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author LENOVO
 */
public class produksi extends javax.swing.JFrame {

    /**
     * Creates new form produksi
     */
    DefaultTableModel model;
    
    public produksi() {
        initComponents();
        koneksi.koneksi();
        loadTable();
        loadKodeProduk();
        setLocationRelativeTo(null);
    }
    
    private void loadKodeProduk() {
        try {
            jComboBox1.removeAllItems();
            String sql = "SELECT kode_produk FROM produk";
            PreparedStatement ps = koneksi.con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                jComboBox1.addItem(rs.getString("kode_produk"));
            }
            
            // Tambahkan pengecekan setelah loading
            if (jComboBox1.getItemCount() > 0) {
                jComboBox1.setSelectedIndex(0);
                getNamaProduk();
            }
            
            rs.close();
            ps.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error loading kode produk: " + e.getMessage());
        }
    }
    
    private void loadTable() {
        // Mendefinisikan judul kolom
        String[] header = {"ID", "Kode Produk", "Nama Produk", "Tanggal", "Jumlah"};
        model = new DefaultTableModel(header, 0);
        jTable1.setModel(model);
        
        try {
            String sql = "SELECT p.id_produksi, p.kode_produk, pr.nama_produk, p.tanggal_produksi, p.jumlah_diproduksi " +
                         "FROM produksi p JOIN produk pr ON p.kode_produk = pr.kode_produk";
            PreparedStatement ps = koneksi.con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                String id = rs.getString("id_produksi");
                String kode = rs.getString("kode_produk");
                String nama = rs.getString("nama_produk");
                String tanggal = rs.getString("tanggal_produksi");
                String jumlah = rs.getString("jumlah_diproduksi");
                
                String[] data = {id, kode, nama, tanggal, jumlah};
                model.addRow(data);
            }
            
            rs.close();
            ps.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error loading table: " + e.getMessage());
        }
    }
    
    private void resetForm() {
        jTextField1.setText("");
        jTextField2.setText("");
        jTextField3.setText("");
        if (jComboBox1.getItemCount() > 0) {
            jComboBox1.setSelectedIndex(0);
            getNamaProduk();
        }
    }
    
    private void getNamaProduk() {
        try {
            // Tambahkan pengecekan untuk mencegah NullPointerException
            if (jComboBox1.getSelectedItem() == null) {
                jTextField1.setText("");
                return;
            }
            
            String kode = jComboBox1.getSelectedItem().toString();
            String sql = "SELECT nama_produk FROM produk WHERE kode_produk = ?";
            PreparedStatement ps = koneksi.con.prepareStatement(sql);
            ps.setString(1, kode);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                jTextField1.setText(rs.getString("nama_produk"));
            } else {
                jTextField1.setText("");
            }
            
            rs.close();
            ps.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error get nama produk: " + e.getMessage());
        }
    }
    
    private void simpanData() {
        try {
            // Cek apakah combo box memiliki item yang dipilih
            if (jComboBox1.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(null, "Pilih kode produk terlebih dahulu!");
                return;
            }
            
            String kode = jComboBox1.getSelectedItem().toString();
            String tanggal = jTextField2.getText();
            String jumlah = jTextField3.getText();
            
            // Validasi input
            if (tanggal.isEmpty() || jumlah.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Semua field harus diisi!");
                return;
            }
            
            // Validasi format tanggal
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                sdf.setLenient(false);
                Date date = sdf.parse(tanggal);
            } catch (ParseException e) {
                JOptionPane.showMessageDialog(null, "Format tanggal harus yyyy-MM-dd (contoh: 2025-04-29)");
                return;
            }
            
            // Validasi jumlah (harus numerik)
            try {
                Integer.parseInt(jumlah);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Jumlah produksi harus berupa angka!");
                return;
            }
            
            String sql = "INSERT INTO produksi (kode_produk, tanggal_produksi, jumlah_diproduksi) VALUES (?, ?, ?)";
            PreparedStatement ps = koneksi.con.prepareStatement(sql);
            ps.setString(1, kode);
            ps.setString(2, tanggal);
            ps.setInt(3, Integer.parseInt(jumlah));
            
            int result = ps.executeUpdate();
            if (result > 0) {
                JOptionPane.showMessageDialog(null, "Data produksi berhasil disimpan!");
                resetForm();
                loadTable();
            } else {
                JOptionPane.showMessageDialog(null, "Gagal menyimpan data produksi!");
            }
            
            ps.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error simpan data: " + e.getMessage());
        }
    }
    
    private void editData() {
        try {
            int row = jTable1.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(null, "Pilih data yang akan diedit!");
                return;
            }
            
            // Cek apakah combo box memiliki item yang dipilih
            if (jComboBox1.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(null, "Pilih kode produk terlebih dahulu!");
                return;
            }
            
            String id = jTable1.getValueAt(row, 0).toString();
            String kode = jComboBox1.getSelectedItem().toString();
            String tanggal = jTextField2.getText();
            String jumlah = jTextField3.getText();
            
            // Validasi input
            if (tanggal.isEmpty() || jumlah.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Semua field harus diisi!");
                return;
            }
            
            // Validasi format tanggal
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                sdf.setLenient(false);
                Date date = sdf.parse(tanggal);
            } catch (ParseException e) {
                JOptionPane.showMessageDialog(null, "Format tanggal harus yyyy-MM-dd (contoh: 2025-04-29)");
                return;
            }
            
            // Validasi jumlah (harus numerik)
            try {
                Integer.parseInt(jumlah);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Jumlah produksi harus berupa angka!");
                return;
            }
            
            String sql = "UPDATE produksi SET kode_produk = ?, tanggal_produksi = ?, jumlah_diproduksi = ? WHERE id_produksi = ?";
            PreparedStatement ps = koneksi.con.prepareStatement(sql);
            ps.setString(1, kode);
            ps.setString(2, tanggal);
            ps.setInt(3, Integer.parseInt(jumlah));
            ps.setInt(4, Integer.parseInt(id));
            
            int result = ps.executeUpdate();
            if (result > 0) {
                JOptionPane.showMessageDialog(null, "Data produksi berhasil diupdate!");
                resetForm();
                loadTable();
            } else {
                JOptionPane.showMessageDialog(null, "Gagal mengupdate data produksi!");
            }
            
            ps.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error edit data: " + e.getMessage());
        }
    }
    
    private void hapusData() {
        try {
            int row = jTable1.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(null, "Pilih data yang akan dihapus!");
                return;
            }
            
            int confirm = JOptionPane.showConfirmDialog(null, "Apakah Anda yakin ingin menghapus data ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                String id = jTable1.getValueAt(row, 0).toString();
                String sql = "DELETE FROM produksi WHERE id_produksi = ?";
                PreparedStatement ps = koneksi.con.prepareStatement(sql);
                ps.setInt(1, Integer.parseInt(id));
                
                int result = ps.executeUpdate();
                if (result > 0) {
                    JOptionPane.showMessageDialog(null, "Data produksi berhasil dihapus!");
                    resetForm();
                    loadTable();
                } else {
                    JOptionPane.showMessageDialog(null, "Gagal menghapus data produksi!");
                }
                
                ps.close();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error hapus data: " + e.getMessage());
        }
    }
    
    private void showSelectedData() {
        try {
            int row = jTable1.getSelectedRow();
            if (row != -1) {
                String kode = jTable1.getValueAt(row, 1).toString();
                jComboBox1.setSelectedItem(kode);
                
                jTextField1.setText(jTable1.getValueAt(row, 2).toString());
                jTextField2.setText(jTable1.getValueAt(row, 3).toString());
                jTextField3.setText(jTable1.getValueAt(row, 4).toString());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error menampilkan data: " + e.getMessage());
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("PRODUKSI");

        jButton1.setText("HOME");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel2.setText("Kode produk");

        jLabel3.setText("Nama Produk");

        jLabel4.setText("Tanggal Produksi");

        jLabel5.setText("Jumlah Produksi");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { }));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        jTextField1.setEditable(false);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jButton2.setText("SIMPAN");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("EDIT");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setText("HAPUS");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(60, 60, 60)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton3)
                        .addGap(18, 18, 18)
                        .addComponent(jButton4)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 527, Short.MAX_VALUE)
                        .addComponent(jButton1)
                        .addGap(37, 37, 37))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel5)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel3))
                                .addGap(42, 42, 42)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextField1)
                                    .addComponent(jTextField2)
                                    .addComponent(jTextField3, javax.swing.GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE)))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 393, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jButton1))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2)
                    .addComponent(jButton3)
                    .addComponent(jButton4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 307, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(112, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>                        

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {                                         
        // TODO add your handling code here:
         home hm = new home();
        hm.setVisible(true);
        
        this.dispose();
    }                                        

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {                                           
        getNamaProduk();
    }                                          

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {                                         
        simpanData();
    }                                        

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {                                         
        editData();
    }                                        

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {                                         
        hapusData();
    }                                        

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {                                     
        showSelectedData();
    }                                    

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(produksi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(produksi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(produksi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(produksi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new produksi().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify                     
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    // End of variables declaration                   
}