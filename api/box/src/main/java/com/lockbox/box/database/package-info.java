/**
 * This package contains components for managing box-related data in the Lockbox application.
 * <p>
 * It includes:
 * <ul>
 *     <li>{@code dto} - Data Transfer Objects for transferring data between layers.</li>
 *     <li>{@code mapper} - Mapper interfaces for converting between entities and DTOs using MapStruct.</li>
 *     <li>{@code model} - Entity classes representing the database schema for boxes.</li>
 *     <li>{@code repository} - Repository interfaces for CRUD operations and database interactions.</li>
 *     <li>{@code service} - Service classes encapsulating business logic and transactional operations.</li>
 * </ul>
 *
 * <p>Primary entity:</p>
 * <ul>
 *     <li>{@link com.lockbox.box.database.model.Box} -
 *              Represents a storage box with details such as owner ID and alias.</li>
 * </ul>
 *
 * <p>Main functionality includes:</p>
 * <ul>
 *     <li>Storing and retrieving box data.</li>
 *     <li>Mapping between database entities and DTOs for efficient data transfer.</li>
 *     <li>Providing service methods to manage box-related operations.</li>
 * </ul>
 *
 * @version 1.0
 * @since 1.0
 */
package com.lockbox.box.database;
