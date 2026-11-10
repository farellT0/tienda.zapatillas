package com.farell.tienda.zapatillas.responseData;

import com.farell.tienda.zapatillas.model.Zapatilla;

import java.util.List;

public class InfoZapatillas {

    private List<Zapatilla> zapatillas;
    private int totalZapatillas;

    public List<Zapatilla> getZapatillas() {
        return zapatillas;
    }

    public void setZapatillas(List<Zapatilla> zapatillas) {
        this.zapatillas = zapatillas;
    }

    public int getTotalZapatillas() {
        return totalZapatillas;
    }

    public void setTotalZapatillas(int totalZapatillas) {
        this.totalZapatillas = totalZapatillas;
    }
}
