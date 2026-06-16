package com.nayepankh.vims.config;

import com.nayepankh.vims.entity.*;
import com.nayepankh.vims.repository.CampaignRepository;
import com.nayepankh.vims.repository.EnrollmentRepository;
import com.nayepankh.vims.repository.VolunteerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.LocalDate;

@Configuration
@Profile("dev")
@RequiredArgsConstructor
@Slf4j
public class DataSeeder {

    @Bean
    CommandLineRunner seedData(VolunteerRepository volunteerRepo,
                               CampaignRepository campaignRepo,
                               EnrollmentRepository enrollmentRepo) {
        return args -> {
            log.info("🌱 Seeding sample data for dev profile...");

            // ── Volunteers ─────────────────────────────────────
            Volunteer v1 = volunteerRepo.save(Volunteer.builder()
                    .name("Aarav Sharma")
                    .email("aarav.sharma@example.com")
                    .phone("+91-9876543210")
                    .city("Delhi")
                    .skills("cooking,logistics,driving")
                    .build());

            Volunteer v2 = volunteerRepo.save(Volunteer.builder()
                    .name("Priya Patel")
                    .email("priya.patel@example.com")
                    .phone("+91-9876543211")
                    .city("Mumbai")
                    .skills("teaching,first-aid,communication")
                    .build());

            Volunteer v3 = volunteerRepo.save(Volunteer.builder()
                    .name("Rohan Gupta")
                    .email("rohan.gupta@example.com")
                    .phone("+91-9876543212")
                    .city("Bangalore")
                    .skills("photography,social-media,design")
                    .build());

            Volunteer v4 = volunteerRepo.save(Volunteer.builder()
                    .name("Ananya Reddy")
                    .email("ananya.reddy@example.com")
                    .phone("+91-9876543213")
                    .city("Hyderabad")
                    .skills("teaching,mentoring,counseling")
                    .build());

            Volunteer v5 = volunteerRepo.save(Volunteer.builder()
                    .name("Vikram Singh")
                    .email("vikram.singh@example.com")
                    .phone("+91-9876543214")
                    .city("Delhi")
                    .skills("logistics,management,driving")
                    .status(VolunteerStatus.INACTIVE)
                    .build());

            // ── Campaigns ──────────────────────────────────────
            Campaign c1 = campaignRepo.save(Campaign.builder()
                    .title("Sunday Food Drive — Chandni Chowk")
                    .type(CampaignType.FOOD_DRIVE)
                    .location("Chandni Chowk, Delhi")
                    .eventDate(LocalDate.now().plusDays(7))
                    .capacity(20)
                    .status(CampaignStatus.UPCOMING)
                    .description("Weekly food distribution drive serving 200+ meals to the homeless community near Chandni Chowk.")
                    .build());

            Campaign c2 = campaignRepo.save(Campaign.builder()
                    .title("Winter Clothing Drive 2026")
                    .type(CampaignType.CLOTHING_DRIVE)
                    .location("Connaught Place, Delhi")
                    .eventDate(LocalDate.now().plusDays(14))
                    .capacity(30)
                    .status(CampaignStatus.UPCOMING)
                    .description("Collecting and distributing warm winter clothing to underprivileged families.")
                    .build());

            Campaign c3 = campaignRepo.save(Campaign.builder()
                    .title("Health Awareness Camp — Dharavi")
                    .type(CampaignType.HEALTH_AWARENESS)
                    .location("Dharavi, Mumbai")
                    .eventDate(LocalDate.now().plusDays(3))
                    .capacity(15)
                    .status(CampaignStatus.ACTIVE)
                    .description("Free health checkups, hygiene awareness, and medicine distribution in partnership with local clinics.")
                    .build());

            Campaign c4 = campaignRepo.save(Campaign.builder()
                    .title("Weekend Tuition Program — Batch 12")
                    .type(CampaignType.EDUCATION)
                    .location("Koramangala, Bangalore")
                    .eventDate(LocalDate.now().plusDays(2))
                    .capacity(10)
                    .status(CampaignStatus.ACTIVE)
                    .description("Free weekend tuition classes for underprivileged children (Grade 5-8).")
                    .build());

            Campaign c5 = campaignRepo.save(Campaign.builder()
                    .title("Diwali Food Distribution 2025")
                    .type(CampaignType.FOOD_DRIVE)
                    .location("India Gate, Delhi")
                    .eventDate(LocalDate.now().minusDays(180))
                    .capacity(50)
                    .status(CampaignStatus.COMPLETED)
                    .description("Special Diwali food distribution event — served 500+ meals.")
                    .build());

            // ── Enrollments ────────────────────────────────────
            // Active enrollments
            enrollmentRepo.save(Enrollment.builder()
                    .volunteer(v1).campaign(c1).status(EnrollmentStatus.REGISTERED).build());

            enrollmentRepo.save(Enrollment.builder()
                    .volunteer(v2).campaign(c1).status(EnrollmentStatus.REGISTERED).build());

            enrollmentRepo.save(Enrollment.builder()
                    .volunteer(v3).campaign(c1).status(EnrollmentStatus.REGISTERED).build());

            enrollmentRepo.save(Enrollment.builder()
                    .volunteer(v2).campaign(c3).status(EnrollmentStatus.REGISTERED).build());

            enrollmentRepo.save(Enrollment.builder()
                    .volunteer(v3).campaign(c4).status(EnrollmentStatus.REGISTERED).build());

            enrollmentRepo.save(Enrollment.builder()
                    .volunteer(v4).campaign(c4).status(EnrollmentStatus.REGISTERED).build());

            // Completed campaign enrollments (with attendance + hours)
            enrollmentRepo.save(Enrollment.builder()
                    .volunteer(v1).campaign(c5).status(EnrollmentStatus.ATTENDED).hoursLogged(6).build());

            enrollmentRepo.save(Enrollment.builder()
                    .volunteer(v2).campaign(c5).status(EnrollmentStatus.ATTENDED).hoursLogged(8).build());

            enrollmentRepo.save(Enrollment.builder()
                    .volunteer(v4).campaign(c5).status(EnrollmentStatus.ATTENDED).hoursLogged(5).build());

            enrollmentRepo.save(Enrollment.builder()
                    .volunteer(v3).campaign(c5).status(EnrollmentStatus.NO_SHOW).build());

            // Cancelled enrollment
            enrollmentRepo.save(Enrollment.builder()
                    .volunteer(v4).campaign(c2).status(EnrollmentStatus.CANCELLED).build());

            log.info("✅ Seed data loaded — {} volunteers, {} campaigns, {} enrollments",
                    volunteerRepo.count(), campaignRepo.count(), enrollmentRepo.count());
        };
    }
}
