package com.cognizant.kafka.partitioner;

import java.util.Map;

import com.cognizant.kafka.domain.Account;
import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;

public class AccountTypePartitioner implements Partitioner{
    @Override
    public void configure(Map<String,?> configs) {}

    @Override
    public int partition(String topic,Object keyObj,byte[] keyBytes,Object valueObj,byte[] valueBytes,Cluster cluster) {
        String accountType=null;

        if(keyObj instanceof String) {
            accountType=(String) keyObj;
        } else if (valueObj instanceof Account) {
            accountType=((Account) valueObj).getAccountType();
        }

        if(accountType==null) {
            accountType="UNKNOWN";
        }
        accountType=accountType.toUpperCase();

        switch (accountType) {
            case "CA": return 0;
            case "SB": return 1;
            case "RD": return 2;
            case "LOAN": return 3;
            default:
                int partitionCount=cluster.partitionCountForTopic(topic);
                return Math.abs(accountType.hashCode())%Math.max(partitionCount,1);
        }
    }

    private String extractAccountTypeFromJsonString(String s) {
        if(s==null) {
            return null;
        }
        String token="\"accountType\"";
        int idx=s.indexOf(token);
        if(idx==-1) {
            return null;
        }
        int colon=s.indexOf(":",idx);
        if(colon==-1) {
            return null;
        }
        int firstQuote=s.indexOf('"',colon);
        if(firstQuote==-1) {
            return null;
        }
        int secondQuote=s.indexOf('"',colon);
        if(secondQuote==-1) {
            return null;
        }
        return s.substring(firstQuote+1,secondQuote);
    }
    @Override
    public void close() {}


}
