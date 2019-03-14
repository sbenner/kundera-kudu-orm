package com.impetus.client.kudu;

public interface KuduRowProcessor<T> {


    public void process(T row);

}
