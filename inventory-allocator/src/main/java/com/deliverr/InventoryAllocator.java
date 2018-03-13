package com.deliverr;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Set;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONObject;
import org.json.JSONArray;

public class InventoryAllocator {
	
	private String input;
	private String strPurchaseJson = null;
	private Hashtable<String,Hashtable<String,Integer>> hashInventory = new Hashtable<String,Hashtable<String,Integer>>();

	InventoryAllocator(String inputLine) {
		setInput(inputLine);
	}
	
	/* Method Name: InventoryAllocationAndPurchase
	 * Main method for calling the Update inventory and Purchase request
	 */
	public String InventoryAllocationAndPurchase() {
		
		if (this.input == null) return null; // Input is null
		if (!updateInventory()) return null; // Update Inventory failed

		return processPurchaseRequest();
	}
	
	/* Method Name: updateInventory
	 * Method for update inventory using the input values
	 */
	private Boolean updateInventory() 
	{
		try {
			String strItemName = null;
			String [] arrItems = null; 
			String [] strJSONStrings = generateJSONStrings(this.input);
	     
			if (strJSONStrings == null)
				return false;
     
			this.strPurchaseJson = strJSONStrings[0]; //assign the purchase JSON string
			for(int iInventory = 1; iInventory < strJSONStrings.length; iInventory++) { // Loop thru the inventories 
				JSONObject inventortdata = new JSONObject(strJSONStrings[iInventory]);
				final String strInventoryName = (String) inventortdata.get("name");  //get the inventory name
				JSONArray aInventortData = inventortdata.getJSONArray("inventory");
	 
				for( int iInvItem = 0; iInvItem < aInventortData.length(); iInvItem++) {
					strItemName = aInventortData.get(iInvItem).toString();
					arrItems =  strItemName.replaceAll("(\\{|\\}|\")", "").split(",");
			 
					for( int iItem = 0; iItem < arrItems.length; iItem++) {
						String []arrItem = arrItems[iItem].split(":");
						strItemName = arrItem[0];
						final Integer iQuantity = Integer.parseInt(arrItem[1]);
					 
						//Put the values into the Inventory
						if ( this.hashInventory.get(strItemName) == null){
							this.hashInventory.put(strItemName,new Hashtable<String,Integer>(){{put(strInventoryName,iQuantity);}}); 
						}
						else {
							Hashtable<String, Integer> hashItmes = this.hashInventory.get(strItemName);
							this.hashInventory.remove(strItemName);
							hashItmes.put(strInventoryName, iQuantity);
							this.hashInventory.put(strItemName, hashItmes);
						}
					}
				}
			}
		} catch( Exception e) {
			e.printStackTrace();
			return null;
		}
 	  return true;
	}
	
	/* Method Name: processPurchaseRequest
	 * Method for process the purchase request
	 */
	private String processPurchaseRequest()
	{
		String strOutputString = "[{";
		try{
			String [] arrItems = strPurchaseJson.replaceAll("(\\{|\\}|\")", "").split(",");
		
			for( int  iItem= 0; iItem < arrItems.length; iItem++) {
				String []arrItem = arrItems[iItem].split(":");
				String strPurchaseItem = arrItem[0];
				Integer iPurchaseQuantity = Integer.parseInt(arrItem[1]);
			 
				System.out.println( "Purchase Item:"+ strPurchaseItem + "  Quantity Required:" + arrItem[1] );
			 
				Hashtable<String, Integer> hashItmes = this.hashInventory.get(arrItem[0]);	 
				if (hashItmes == null) { 
					System.out.println( "Purchase Item "+ strPurchaseItem + " is not available" );
					continue;
				}
				else {
					Set<String> setInventrories = hashItmes.keySet();
					for(String strInventory: setInventrories) {
						Integer iItemQuantiry = hashItmes.get(strInventory);
					 
						if (iItemQuantiry== 0) 	break; //no items available 
						else if (iPurchaseQuantity <= iItemQuantiry) { // purchase quantity is same or inventory quantity.
							strOutputString = strOutputString +  strInventory + ": {" + strPurchaseItem + ": " + iPurchaseQuantity.toString() + "},";
							break;
						}
						else { // not enough purchase items
							strOutputString = strOutputString +  strInventory + ": {" + strPurchaseItem + ": " + iItemQuantiry.toString() + "},";
							iPurchaseQuantity = iPurchaseQuantity -iItemQuantiry;
						}
					}
				}
		    }
			strOutputString = strOutputString.substring(0, strOutputString.length()-1);
			if (strOutputString.contentEquals("[") == true)  strOutputString = strOutputString + "]";
			else  strOutputString = strOutputString + "}]";
	  } catch( Exception e) {
			e.printStackTrace();
			return null;
	  }
	  return strOutputString;
	}
	
	/* Method Name: generateJSONStrings
	 * Method for generate the JSON Strings. It will format the input and generate the JSON strings.
	 */
	private String [] generateJSONStrings(String strInput)
	{
		String [] strFormattedJSON = null; 
		try {
			strInput = strInput.replaceAll("\\s+",""); // Remove white spaces 
			strInput  = strInput.replaceAll("\\},\\[", "\\}\\$\\$\\["); // Add separator for split purchase and inventory string  
			String [] arrayInputs = strInput.split("\\$\\$");
		
			/* Parsing and generate JSON strings*/
			String jsonInventoryString = arrayInputs[1].replaceAll("\\w+", "\"$0\"");
			jsonInventoryString =  jsonInventoryString.replaceAll("(\\[|\\])", "");
        
			jsonInventoryString  =  jsonInventoryString.replaceAll("\\}},", "\\}\\}\\$\\$");
			String []arryInventrory = jsonInventoryString.split("\\$\\$");
        
			Integer iTotalInventories = arryInventrory.length;
			strFormattedJSON = new String [iTotalInventories +1]; 
		
			String jsonPurchaseString = arrayInputs[0].replaceAll("\\w+", "\"$0\"");
        
			if (isValidJSON(jsonPurchaseString)) {
				System.out.println( "Purchase Json:"+ jsonPurchaseString );
				strFormattedJSON[0] = jsonPurchaseString; // Assign Purchase JSON String
			}
			else{
				System.out.println( "Invalid Purchase Input Format :" +  jsonPurchaseString);
				return null;
			}
        
			for ( int i = 0; i < iTotalInventories; i++) {
				String strInventory = arryInventrory[i];

				strInventory = strInventory.replaceAll("(inventory\":)", "$0[");
				strInventory = strInventory.replaceAll("\"}", "$0]");

				if (isValidJSON(strInventory)) {
					System.out.println( "Inventroy Json:"+ strInventory );
					strFormattedJSON[i+1] = strInventory; // Assign Inventory JSON String
				} 
				else {
					System.out.println( "Invalid Inventory Input Format :" +  strInventory);
					return null;
				}
			}
		}catch( Exception e) {
			e.printStackTrace();
			return null;
		}
        return strFormattedJSON;
	}
	
	/* Method Name: isValidJSON
	 * Method for validate the JSON Strings. 
	 */
	private boolean isValidJSON(final String json) {
		   boolean valid = false;
		   try {
		      final JsonParser parser = new ObjectMapper().getFactory()
		            .createParser(json);
		      while (parser.nextToken() != null) {
		      }
		      valid = true;
		   } catch (JsonParseException jpe) {
		      jpe.printStackTrace();
		   } catch (IOException ioe) {
		      ioe.printStackTrace();
		   }
	  return valid;
	}
	
	/* Method Name: setInput
	 * Method for set the input value 
	 */
 	private void setInput(String input) {
		this.input = input;
	}
}
