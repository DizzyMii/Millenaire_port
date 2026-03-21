package org.dizzymii.millenaire2.parity;

/**
 * Result of evaluating a parity contract.
 */
public record ParityContractResult(ParityContract contract, boolean passed, String details) {
}
