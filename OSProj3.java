/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package osproj3;

import java.util.*;
public class OSProj3 {
//Create a globlca disk array of size 256 with 512 bytes each, along with a duplicate array which will read and write to the disk
//by block. 
public static byte[][] disk = new byte[256][512];
public static byte[][] b2 = new byte[256][512];
    public static void main(String[] args) {
        //Create new allocation object and set to null 
        Allocation alloc = null;
        //Based on the string entered through the command line, we will allocate a new object using the strategy
        //interface. 
        if("Contiguous".equals(args[0])){
        alloc = new Allocation(new Contiguous());  
        }
        else if("Chained".equals(args[0])){
        alloc = new Allocation(new Chained());  
        }
        else if("Indexed".equals(args[0])){
        alloc = new Allocation(new Indexed());  
        }
        //Set a choice and block choice integer to get user input. 
        int choice = -1;
        int blockchoice = -1;
        b2[1][1] = 1; //intialize bitmap to 1, as this will always be true. 
        disk[1] = b2[1]; //Write to disk by block 
        Scanner scan = new Scanner(System.in);
        //Menu which will run as long as the choice is not 8 
         while(choice != 8){
             //List of options for user to select from as outlined in project description. 
             System.out.println("1. Display a File:");
             System.out.println("2. Display File Table:");
             System.out.println("3. Display Bitmap:");
             System.out.println("4. Display Disk Block:");
             System.out.println("5. Copy from simulation to real system:");
             System.out.println("6. Copy from real system to simulation:");
             System.out.println("7. Delete a File:");
             System.out.println("8. Exit Simulation:");
             //Scan in choice from user input and switch case it. 
             choice = scan.nextInt();
             switch(choice){
                 //If 1, then call the readFile function. 
                 case 1:
                     alloc.readFile(disk);
                     break;
                //If 2, then display the file table. 
                 case 2:
                     //Read in block 0 from the disk into the dupicate array. 
                     b2[0] = disk[0];
                     //for the whole block, if the byte is not empty, then print out the file table. If i mod 13 is 9 or i 
                     //mod 13 is 11, that means that we have encountered either the starting block or number of blocks. Therefore
                     //we need to use the &0xFF to display the proper integer. 
                     for(int i = 0; i < 512; i++){
                         if(b2[0][i] != 0){
                            if ((i % 13 == 9 || i % 13 == 11) && i > 0){ 
                                System.out.print(b2[0][i]&0xFF);
                            }
                            else
                            System.out.print((char)b2[0][i]);
                         }
                     }
                     break;
                //If 3, display the bitmap. 
                 case 3:
                     //Read in block 1 from the disk into the dupicate array. 
                     b2[1] = disk[1];
                     //for the whole disk, if the character present is a 1, print a 1, else print a 0. 
                     for(int i = 0; i < 256; i++){
                         if(b2[1][i] == 1){
                            System.out.print(1);
                         }
                         else{
                             System.out.print(0);
                         }
                     //Print a newline after each set of 20 characters 
                     if (i % 20 == 0 && i != 0){
                         System.out.print("\n");
                     }      
                     }
                     System.out.print("\n");
                     break;
                //If 4, display a block. 
                 case 4:
                     //Get a blockchoice from the user. 
                     System.out.println("Select block to view: ");
                     //If the block is invalid, then keep looping until it is. 
                     blockchoice = scan.nextInt();
                     while(blockchoice < 0 || blockchoice > 255){
                      System.out.println("Select block to view: ");
                     blockchoice = scan.nextInt();
                     }
                     //Read in blockchoice from the disk into the dupicate array. 
                     b2[blockchoice] = disk[blockchoice];
                     //For the whole block, print out the ascii values inside of that block. 
                     for(int i = 0; i < 512; i++){
                     System.out.print(b2[blockchoice][i]);
                     //Print a newline after each set of 20 characters 
                     if (i % 20 == 0 && i != 0){
                         System.out.print("\n");
                     }      
                     }
                     System.out.print("\n");
                     break;
                //If 5, send a file to the real system. 
                 case 5:
                     //Call method 
                     alloc.sendFile(disk);
                     break;
                //If 6, add a file to the simulation. 
                 case 6:
                     //Call method. 
                     alloc.executeStrategy(disk);
                     break;
                //If 7, delete a file. 
                 case 7:
                     //Call method. 
                     alloc.deleteFile(disk);
                     break;
                //If 8, exit the simulation. 
                 case 8:
                     System.exit(0);
                     break;
                //Else, go back to the menu. 
                 default:
                     break;
                 
             }
                     
         }
    }
    
}
