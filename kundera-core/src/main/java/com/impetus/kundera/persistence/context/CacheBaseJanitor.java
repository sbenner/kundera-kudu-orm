package com.impetus.kundera.persistence.context;

import com.impetus.kundera.graph.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.stream.Collectors;

public class CacheBaseJanitor implements Runnable {
    final static Logger logger = LoggerFactory.getLogger(CacheBaseJanitor.class);

    Map<String, Node> nodeCache;
    CacheBase cacheBase;

    public CacheBaseJanitor(Map<String, Node> nodeCache, CacheBase cacheBase) {
        this.nodeCache = nodeCache;
        this.cacheBase = cacheBase;

        logger.info(this.getClass().getName() + " started..");
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            final long currentTime = System.currentTimeMillis();
            nodeCache.
                    values().stream().filter(o ->
                    {
                        long millis = currentTime - o.getCreationTime();
                        return millis >= 120000;
                    }).collect(Collectors.toList()).forEach(
                            cacheBase::removeNodeFromCache
                    );
            try {
                Thread.sleep(30000);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }


    }
}
