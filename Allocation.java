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
public class Allocation {
   //Create a strategy object and disk duplication array. 
   private Strat strategy;
   public static byte[][] disk = new byte[256][512];
   public Allocation(Strat strategy){
      //Set strategy based on command line argument. 
      this.strategy = strategy;

   }
   //The disk is passed in as an argument in each function. 
   //Main execution function. Will add a file to the simulation. Calls the doOp funciton. 
   public void executeStrategy(byte[][]disk){
         this.disk = disk;
         strategy.doOp(disk);
   }
   //Calls the deleteFile function 
    public void deleteFile(byte[][]disk){
        this.disk = disk;
        strategy.deleteFile(disk);
    }
    //Calls the readFile function 
    public void readFile(byte[][]disk){
        this.disk = disk;
        strategy.readFile(disk);
    }
    //Calls the sendFile function 
    public void sendFile(byte[][]disk){
        this.disk = disk;
        strategy.sendFile(disk);
    }
}
