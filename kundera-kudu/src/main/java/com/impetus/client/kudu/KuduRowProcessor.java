package com.impetus.client.kudu;

public interface KuduRowProcessor<T> {


    void process(T row);

}
