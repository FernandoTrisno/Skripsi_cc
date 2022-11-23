package com.example.skripsi_cc;

import android.widget.Button;
import android.widget.CheckBox;

public class ListMakanUser {
    private String nama_makanan;
    private String jumlah_kalori;
    private String porsi;
    private Button edit;
    private Button delete;
    private boolean selected;
    private long tanggal;

    public long getTanggal() {
        return tanggal;
    }

    public void setTanggal(long tanggal) {
        this.tanggal = tanggal;
    }

    ListMakanUser(String jumlah_kalori){
        this.jumlah_kalori = jumlah_kalori;
    }


    ListMakanUser(String nama_makanan, String porsi, String jumlah_kalori){
        this.nama_makanan = nama_makanan;
        this.jumlah_kalori = jumlah_kalori;
        this.porsi = porsi;
    }

    public String getNama_makanan() {
        return nama_makanan;
    }

    public void setNama_makanan(String nama_makanan) {
        this.nama_makanan = nama_makanan;
    }

    public String getJumlah_kalori() {
        return jumlah_kalori;
    }

    public void setJumlah_kalori(String jumlah_kalori) {
        this.jumlah_kalori = jumlah_kalori;
    }

    public String getPorsi() {
        return porsi;
    }

    public void setPorsi(String porsi) {
        this.porsi = porsi;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
       selected = selected;
    }


    public Button getEdit() {
        return edit;
    }

    public void setEdit(Button edit) {
        this.edit = edit;
    }

    public Button getDelete() {
        return delete;
    }

    public void setDelete(Button delete) {
        this.delete = delete;
    }

}
