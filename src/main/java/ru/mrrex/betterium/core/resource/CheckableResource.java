package ru.mrrex.betterium.core.resource;

import ru.mrrex.betterium.core.checksum.ChecksumAlgorithm;

import java.util.Map;

public interface CheckableResource {

    Map<ChecksumAlgorithm, Long> getChecksums();
}
