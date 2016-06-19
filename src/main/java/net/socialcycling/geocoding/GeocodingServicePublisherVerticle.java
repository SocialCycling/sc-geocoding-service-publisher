package net.socialcycling.geocoding;


import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.types.HttpEndpoint;

public class GeocodingServicePublisherVerticle extends AbstractVerticle {

    private ServiceDiscovery discovery;
    private Record record;


    @Override
    public void start(Future<Void> startFuture) throws Exception {
        discovery = ServiceDiscovery.create(vertx);
        record = HttpEndpoint.createRecord(
                "net.socialcycling.geocoding",
                config().getString("nominatim.host"),
                config().getInteger("nominatim.port"),
                config().getString("nominatim.root")
        );
        discovery.publish(record, ar -> {
            if (ar.succeeded()) {
                record = ar.result();
                startFuture.complete();
            } else {
                startFuture.fail(ar.cause());
            }
        });
    }

    @Override
    public void stop(Future<Void> stopFuture) throws Exception {
        discovery.unpublish(record.getRegistration(), ar -> {
            if (ar.succeeded()) {
                stopFuture.complete();
            } else {
                stopFuture.fail(ar.cause());
            }
        });
    }
}
