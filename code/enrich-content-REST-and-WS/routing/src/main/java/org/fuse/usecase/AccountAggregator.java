package org.fuse.usecase;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.apache.cxf.message.MessageContentsList;
import org.globex.Account;
import org.globex.Company;
import org.globex.CorporateAccount;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Aggregator implementation which extract the id and salescontact
 * from CorporateAccount and update the Account
 */
public class AccountAggregator implements AggregationStrategy {

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
    	System.out.println(">>>>>>>>>>>>>>>>> INSIDE AGGREGATOR");
    	
    	if (oldExchange == null){
    		// must be first run
    		System.out.println(">>> oldExchange was null\n");
    		return newExchange;
    	}
    	
    	System.out.println("+++++++++++++++++++++++ oldExchange was NOT NULL");
    	
    	Account account = oldExchange.getIn().getBody(Account.class);
    	
    	System.out.println("+++++++++++++++++++++++ Created account object");
    	
    	CorporateAccount corpAccount = newExchange.getIn().getBody(CorporateAccount.class);
    	
    	System.out.println("+++++++++++++++++++++++ Created corpAccount object");
    	
    	System.out.println(">> newExchange object: " + newExchange.getIn().getBody());
    	
    	account.setClientId(corpAccount.getId());
    	account.setSalesRepresentative(corpAccount.getSalesContact());
    	
    	// since we aren't running parallel, we believe this will always be Account type
    	oldExchange.getIn().setBody(account, Account.class);
        return oldExchange;
    }
    
}