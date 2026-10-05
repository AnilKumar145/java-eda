---
title: "Diagnostic Image Parallel Tile Processor"
type: app_lab
module: concurrency
unit: unit_2_6_concurrency_problems_and_patterns
lab_number: 1
difficulty: easy
use_case: diagnostic_image_parallel_tile_processor
domain: healthcare
order: 1
duration_hours: 2
tags:
  topics:
    - concurrency
    - fork-join
  subtopics:
    - recursive-task
    - work-stealing
    - image-tiles
    - parallel-processing
---

# Lab Level 1: Diagnostic Image Parallel Tile Processor (Java)
**Module**: Concurrency
**Objective**: Build a recursive divide-and-conquer image matrix processor using Java's `ForkJoinPool` and `RecursiveTask` to compute pixel intensity statistics across high-resolution medical scans.
**Difficulty**: Easy
**Context**: Hospital Radiology DICOM Image Preprocessing Unit

## Generic Information
**Problem Statement**: High-resolution MRI and CT scans contain millions of grayscale pixel values. In order to detect abnormal tissue density, radiology image pipelines must calculate the total pixel intensity and count hot pixels (pixels exceeding a threshold) across the entire matrix. Processing this sequentially causes noticeable lag on the radiologist's workstation. We will implement a divide-and-conquer `RecursiveTask` that splits image matrices into sub-tiles until small enough to process sequentially, leveraging CPU work-stealing.

**Goals**:
- Implement `RecursiveTask<TileStats>` to compute total intensity and hot-pixel count.
- Split tiles recursively until length $\le$ `THRESHOLD`.
- Verify multi-core parallel speedup and statistical accuracy.

## Use Case
**Title**: Parallel DICOM Scan Pixel Density Analysis
**Description**: Deconstruct a flat 1D image pixel array into recursive sub-ranges, computing total intensity and abnormal high-density tissue counts.

### Rules
- Must extend `RecursiveTask<TileStats>`.
- Workload split must use `fork()` on left branch and `compute()` on right branch.

### Test Cases
- Case 1: Compute statistics for an image array of 10,000 pixels. Verify exact intensity sum and hot pixel count.
- Case 2: Verify threshold leaf node behavior.
