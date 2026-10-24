package com.buildcraftneo.industry;

/**
 * IC2-style block-side EU store, exposed to {@link EuCableBlock} networks so
 * energy can flow between machines (bat_box, charge_pad, ...). Stored EU is
 * kept in the machine's MJ buffer 1:1.
 */
public interface EuStorage {
    long euStored();

    long euCapacity();

    /** Add up to {@code amount} EU, returns how much was accepted. */
    long insertEu(long amount);

    /** Remove up to {@code amount} EU, returns how much was drained. */
    long drainEu(long amount);
}
