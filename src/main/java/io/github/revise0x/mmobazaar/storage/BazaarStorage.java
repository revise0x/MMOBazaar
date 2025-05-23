package io.github.revise0x.mmobazaar.storage;

import io.github.revise0x.mmobazaar.bazaar.BazaarData;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface BazaarStorage {
    void init();

    void saveBazaar(BazaarData data);

    void deleteBazaar(UUID bazaarId);

    void saveAll(Collection<BazaarData> bazaars);

    Optional<BazaarData> loadBazaar(UUID bazaarId);

    Collection<BazaarData> loadAll();
}