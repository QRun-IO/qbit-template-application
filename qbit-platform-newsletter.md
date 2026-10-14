# qbit-platform-newsletter Technical Specification

## 1. Overview

The `qbit-platform-newsletter` QBit handles a double opt-in newsletter subscription system. It manages subscription lifecycles, campaign management, and integrates with an outbound email provider.

## 2. Data Models

### 2.1. Tables

1.  **`subscription`**:
    *   `id` (UUID, PK)
    *   `email` (VARCHAR, UNIQUE, NOT NULL)
    *   `first_name` (VARCHAR, NULLABLE)
    *   `last_name` (VARCHAR, NULLABLE)
    *   `status` (VARCHAR, NOT NULL, DEFAULT 'unconfirmed') -- 'unconfirmed', 'confirmed', 'unsubscribed', 'bounced'
    *   `confirmation_token` (VARCHAR, UNIQUE, NULLABLE)
    *   `confirmation_expires_at` (TIMESTAMP, NULLABLE)
    *   `unsubscribed_at` (TIMESTAMP, NULLABLE)
    *   `bounce_at` (TIMESTAMP, NULLABLE)
    *   `created_at`, `updated_at`
    *   *Indexes*: `email`, `status`

2.  **`campaign`**:
    *   `id` (UUID, PK)
    *   `name` (VARCHAR, NOT NULL)
    *   `subject` (VARCHAR, NOT NULL)
    *   `body_template` (TEXT, NOT NULL) -- HTML or Markdown template
    *   `segment_query` (TEXT, NULLABLE) -- Criteria for selecting subscribers
    *   `status` (VARCHAR, NOT NULL, DEFAULT 'draft') -- 'draft', 'scheduled', 'sending', 'sent', 'cancelled'
    *   `scheduled_at` (TIMESTAMP, NULLABLE)
    *   `sent_at` (TIMESTAMP, NULLABLE)
    *   `created_at`, `updated_at`

3.  **`campaign_run`**: (Tracks individual sends of a campaign)
    *   `id` (UUID, PK)
    *   `campaign_id` (UUID, FK to `campaign.id`)
    *   `started_at` (TIMESTAMP, NOT NULL)
    *   `completed_at` (TIMESTAMP, NULLABLE)
    *   `total_recipients` (INTEGER, DEFAULT 0)
    *   `sent_count` (INTEGER, DEFAULT 0)
    *   `open_count` (INTEGER, DEFAULT 0)
    *   `click_count` (INTEGER, DEFAULT 0)
    *   `bounce_count` (INTEGER, DEFAULT 0)

4.  **`campaign_recipient`**:
    *   `id` (UUID, PK)
    *   `campaign_run_id` (UUID, FK to `campaign_run.id`)
    *   `subscription_id` (UUID, FK to `subscription.id`)
    *   `sent_at` (TIMESTAMP, NULLABLE)
    *   `opened_at` (TIMESTAMP, NULLABLE)
    *   `clicked_at` (TIMESTAMP, NULLABLE)
    *   `bounced` (BOOLEAN, DEFAULT FALSE)
    *   *Indexes*: `(campaign_run_id, subscription_id)`

5.  **`outbound_email`**: (Log of emails sent *by* this QBit, distinct from provider logs)
    *   `id` (UUID, PK)
    *   `provider` (VARCHAR, NOT NULL) -- e.g., 'smtp', 'sendgrid', 'mailgun'
    *   `provider_message_id` (VARCHAR, NULLABLE) -- ID from the external provider
    *   `recipient_email` (VARCHAR, NOT NULL)
    *   `subject` (VARCHAR, NOT NULL)
    *   `status` (VARCHAR, NOT NULL) -- 'pending', 'sent', 'failed', 'bounced'
    *   `error_message` (TEXT, NULLABLE)
    *   `sent_at`, `created_at`
    *   *Indexes*: `(provider, provider_message_id)`, `recipient_email`

6.  **`email_suppression`**:
    *   `id` (UUID, PK)
    *   `email` (VARCHAR, UNIQUE, NOT NULL)
    *   `reason` (VARCHAR, NOT NULL) -- e.g., 'hard_bounce', 'complaint', 'manual_unsub'
    *   `suppressed_at` (TIMESTAMP, NOT NULL, DEFAULT CURRENT_TIMESTAMP)
    *   *Indexes*: `email`

## 3. Gen-2 Producer Implementation Patterns

-   **DDL Management:** Distribution owns 100% of DDL via Liquibase. This QBit ships only metadata.
-   **Double Opt-In:**
    -   On signup, create a `subscription` record with `status='unconfirmed'` and generate a `confirmation_token`.
    -   Send a confirmation email containing a unique link using the token.
    -   Upon clicking the link, update `subscription.status` to `confirmed` and clear token/expiration.
-   **Campaign Management:**
    -   Define campaigns with templates and optional segmentation rules.
    -   A scheduled job or manual trigger initiates `campaign_run`.
    -   The system queries `subscription` records based on `segment_query` (if provided) or selects all `confirmed` subscribers.
    -   For each selected subscriber, create a `campaign_recipient` record and queue an email via the outbound provider SPI.
-   **Outbound Email Provider SPI:**
    -   Abstract interface for sending emails. Default implementation uses SMTP.
    -   Can be extended to integrate with services like SendGrid, Mailgun, AWS SES.
    -   Requires configuration for provider credentials and endpoints.
-   **Email Tracking:** Support for open (tracking pixel) and click (redirect links) tracking.

## 4. Customizer Slots

-   **`pre_subscription_create`**: Hook before a new subscription record is created. Can be used for initial validation or enrichment.
-   **`post_confirmation_success`**: Triggered after a subscription is confirmed. Useful for triggering welcome emails or CRM updates.
-   **`pre_campaign_send`**: Called before sending a campaign to a batch of recipients. Allows for final checks or modifications to the campaign runner.
-   **`post_email_send`**: Executed after an email is queued/sent via the provider. Used for logging in `outbound_email` and updating `campaign_recipient` status.

## 5. Security Lock Configurations

-   **Subscription Token Security:** Confirmation tokens must be unique, time-limited, and invalidated immediately after use to prevent replay attacks.
-   **Email Suppression:** Maintain a strict `email_suppression` list enforced across all sending operations to comply with anti-spam regulations. Emails to suppressed addresses should be prevented regardless of subscription status.
-   **Provider Credentials Management:** Secure storage and access control for outbound email provider API keys/credentials.

## 6. Integration Points

-   **CRM Bridge:**
    -   **Synchronization:** Periodically or event-driven, sync `subscription` data (email, name, status) to the CRM's contact or lead objects.
    -   **Triggering:** Map CRM status changes (e.g., 'unsubscribed in CRM') back to this QBit to update `subscription.status` and add to `email_suppression`.
-   **Outbound Email Provider:** Integration via SPI (e.g., SMTP, SendGrid API).
-   **Frontend Forms:** Endpoints for subscription signup, confirmation link handling, and unsubscribe requests.

## 7. CRM Rehabilitation Strategy - Detailed Steps Below

This QBit's integration with the CRM is crucial for contact synchronization. The rehabilitation strategy focuses on ensuring this new QBit can seamlessly interact with the post-migration CRM.
