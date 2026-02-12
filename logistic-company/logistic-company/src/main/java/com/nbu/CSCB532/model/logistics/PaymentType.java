package com.nbu.CSCB532.model.logistics;

/**
 * Кой плаща доставката: подател при изпращане или получател при доставка.
 */
public enum PaymentType {
    /** Плаща подателят при изпращане */
    SENDER_PAYS,
    /** Плаща получателят при доставка (наложен платеж) */
    RECIPIENT_PAYS
}
