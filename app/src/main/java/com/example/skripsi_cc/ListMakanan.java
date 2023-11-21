package com.example.skripsi_cc;

import android.text.style.DynamicDrawableSpan;
import android.widget.CheckBox;

public class ListMakanan {
    private String nama_makanan;
    private String jumlah_kalori;
    private String porsi;
    private String gram;
    private CheckBox checkBoxlist;
    private boolean selected;
    private long tanggal;

    public long getTanggal() {
        return tanggal;
    }

    public void setTanggal(long tanggal) {
        this.tanggal = tanggal;
    }

    ListMakanan(String nama_makanan, String porsi, String jumlah_kalori,String gram){
        this.nama_makanan = nama_makanan;
        this.jumlah_kalori = jumlah_kalori;
        this.porsi = porsi;
        this.gram = gram;
    }

    ListMakanan(CheckBox checkBoxlist){
        this.checkBoxlist = checkBoxlist;
    }

    ListMakanan (String nama_makanan){
        this.nama_makanan = nama_makanan;
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

    public String getGram() {
        return gram;
    }

    public void setGram(String gram) {
        this.gram = gram;
    }


    public CheckBox getCheckBoxlist() {
        return checkBoxlist;
    }

    public void setCheckBoxlist(CheckBox checkBoxlist) {
        this.checkBoxlist = checkBoxlist;
    }
}
