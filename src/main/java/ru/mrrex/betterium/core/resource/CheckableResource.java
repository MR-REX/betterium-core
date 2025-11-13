package ru.mrrex.betterium.core.resource;

import ru.mrrex.betterium.core.checksum.ChecksumAlgorithm;
import ru.mrrex.betterium.core.hash.HashAlgorithm;

import java.util.Map;

public interface CheckableResource {

    Map<ChecksumAlgorithm, Long> getChecksums();
    Map<HashAlgorithm, String> getHashes();
}
