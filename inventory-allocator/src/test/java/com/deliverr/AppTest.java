package com.deliverr;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
	
	 /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * IncorrectInputFormat Test :-)
     */
    public void testIncorrectInputFormat()
    { 
    	InventoryAllocator inAll = new InventoryAllocator("{ apple: 5, banana: 5, orange: 5 } , [ { name: owd, inventory: { apple: 5, orange: 10 } }, { name: dm:, inventory: { banana: 5, orange: 10 } } ]");
    	String nRetValue = inAll.InventoryAllocationAndPurchase();
    	assertNull(nRetValue);
    }
    
    /**
     * MultiplePurchase Test :-)
     */
    public void testMultiplePurchase()
    { 
    	InventoryAllocator inAll = new InventoryAllocator("{ apple: 5, banana: 5, orange: 5 } , [ { name: owd, inventory: { apple: 5, orange: 10 } }, { name: dm, inventory: { banana: 5, orange: 10 } } ]");
    	String nRetValue = inAll.InventoryAllocationAndPurchase();
 	
    	assertEquals(nRetValue,"[{owd: {apple: 5},dm: {banana: 5},owd: {orange: 5}}]");
    }
    
    /**
     * InventoryMatch Test :-)
     */
    public void testInventoryMatch()
    { 
    	InventoryAllocator inAll = new InventoryAllocator("{ apple: 1 }, [{ name: owd, inventory: { apple: 1 } }]");
    	String nRetValue = inAll.InventoryAllocationAndPurchase();
    	assertEquals(nRetValue,"[{owd: {apple: 1}}]");
    	
    }
    
    
    /**
     * NotenoughInventory Test :-)
     */
    public void testNotenoughInventory ()
    { 
    	InventoryAllocator inAll = new InventoryAllocator("{ apple: 1 }, [{ name: owd, inventory: { apple: 0 } }]");
    	String nRetValue = inAll.InventoryAllocationAndPurchase();
    	assertEquals(nRetValue,"[]");
    }
    
    /**
     * NotenoughInventory Test :-)
     */
    public void testItemAcrossWarehouses ()
    { 
    	InventoryAllocator inAll = new InventoryAllocator("{ apple: 10 }, [{ name: owd, inventory: { apple: 5 } }, { name: dm, inventory: { apple: 5 }}]");
    	String nRetValue = inAll.InventoryAllocationAndPurchase();
    	assertEquals(nRetValue,"[{owd: {apple: 5},dm: {apple: 5}}]");
    }
  
}
