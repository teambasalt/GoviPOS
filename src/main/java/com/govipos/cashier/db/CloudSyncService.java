package com.govipos.cashier.db;

import com.govipos.cashier.model.RefundRequest;
import com.govipos.cashier.model.SaleTransaction;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Stub contract for background synchronization with the Python FastAPI + PostgreSQL cloud backend.
 */
public interface CloudSyncService {

    CompletableFuture<Boolean> isOnline();

    CompletableFuture<Boolean> syncSingleSale(SaleTransaction sale);

    CompletableFuture<Integer> syncAllPendingSales(List<SaleTransaction> pendingSales);

    CompletableFuture<Boolean> submitRefundRequest(RefundRequest refundRequest);
}
