# qbit-platform-identity Technical Specification

## 1. Overview

The `qbit-platform-identity` QBit provides OpenID Connect (OIDC) integration and manages user profiles. It supports Just-In-Time (JIT) provisioning keyed to the OIDC `sub` field and includes a session customizer for managing security keys.

## 2. Data Models

### 2.1. `user_profile` Table

| Column Name     | Data Type | Constraints                          | Description                                       |
|-----------------|-----------|--------------------------------------|---------------------------------------------------|
| `id`            | UUID      | PRIMARY KEY                          | Unique identifier for the user profile.           |
| `oidc_sub`      | VARCHAR   | UNIQUE, NOT NULL                     | Subject identifier from the OIDC provider.        |
| `email`         | VARCHAR   | UNIQUE, NULLABLE                     | User's email address.                             |
| `given_name`    | VARCHAR   | NULLABLE                             | User's given name.                                |
| `family_name`   | VARCHAR   | NULLABLE                             | User's family name.                               |
| `preferred_username` | VARCHAR | NULLABLE                             | User's preferred username.                        |
| `email_verified`| BOOLEAN   | NOT NULL, DEFAULT FALSE              | Flag indicating if email has been verified.       |
| `profile`       | JSON      | NULLABLE                             | Arbitrary user profile attributes.                |
| `created_at`    | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP  | Timestamp of profile creation.                    |
| `updated_at`    | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP  | Timestamp of last profile update.                 |

## 3. Gen-2 Producer Implementation Patterns

The `qbit-platform-identity` QBit will implement the Gen-2 `QBitMetaDataProducer` pattern.

-   **DDL Management:** Distribution owns 100% of DDL via Liquibase. This QBit will ship only metadata.
-   **OIDC Integration:**
    -   Requires configuration of OIDC client in Keycloak.
    -   JIT provisioning logic will be triggered on successful OIDC authentication.
    -   The `sub` claim from the OIDC token will be used as the unique key to identify or create a user profile.
-   **Session Customizer:**
    -   A customizer will be implemented to enhance OIDC tokens with session-specific security keys or permissions.
    -   This customizer will be invoked post-authentication.

## 4. Customizer Slots

-   **`post_authentication_customizer`**: This slot will be used to:
    -   Fetch or create the `user_profile` based on the `sub` claim.
    -   Map OIDC claims (e.g., `email`, `given_name`, `family_name`, `preferred_username`) to the `user_profile` fields during JIT provisioning or updates.
    -   Generate/retrieve security keys relevant to the user's session and roles.

## 5. Security Lock Configurations

-   **OIDC `sub` Uniqueness:** Enforced by a unique constraint on the `oidc_sub` column in the `user_profile` table.
-   **Hybrid RBAC:** Integration with Keycloak realm roles and `qbit-user-role-permissions` for fine-grained access control.
    -   OIDC claims will be mapped to Keycloak roles.
    -   The `post_authentication_customizer` will resolve `sub` to `userProfileId` and fetch associated permissions.
-   **Session Security Keys:** Managed via the session customizer. Specific implementation details depend on the security key mechanism (e.g., JWT signing keys, API access tokens).

## 6. Integration Points

-   **OIDC Provider (Keycloak):** Acts as the primary authentication authority.
-   **`qbit-platform-user-role-permissions`:** (Assumed QBit) For managing fine-grained permissions. The `post_authentication_customizer` will query this QBit to determine user access rights.
-   **Downstream QBits:** Other QBits will integrate by requesting user identity and permissions information, typically receiving a `userProfileId` and associated RBAC data.

## 7. CRM Rehabilitation Strategy - N/A for this QBit

This QBit is new and does not directly involve CRM rehabilitation. Its integration with CRM would be through user data synchronization, which is handled by other QBits if needed.
