/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package osproj3;

/**
 *
 * @author jklei
 */
public interface Strat {
    //Strategy interface with the four function which are shared between the three different allocation classes. 
    public void doOp(byte [][] disk);
    public void deleteFile(byte [][] disk);
    public void readFile(byte [][]disk);
    public void sendFile(byte [][]disk);
}
