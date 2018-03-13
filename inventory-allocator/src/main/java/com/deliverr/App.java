package com.deliverr;

/**
 * Application for inventory Allocation !
 *
 */

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class App 
{
    public static void main( String[] args ) throws IOException
    {
    	
    	BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Sampel Input format is { apple: 1 }, [{ name: owd, inventory: { apple: 1 } }]");
        System.out.print( "Enter the input:"  );
        String inputLine = reader.readLine();
        reader.close();

    	InventoryAllocator inAll = new InventoryAllocator(inputLine);
    	String strItemAvailabilty = inAll.InventoryAllocationAndPurchase();
        if ( strItemAvailabilty == null) {
        	System.out.println( "Inventory Allocation Failed....!!!" );
        }
        else {
        	System.out.println( "Output: " + strItemAvailabilty );
        }
    }
}
