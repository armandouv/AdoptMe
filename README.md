# AdoptMe

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
AdoptMe is an Android app that allows users to find pets for adoption.

### App Evaluation

- **Category:** Social
- **Mobile:** Android-only application
- **Story:** Allow users to see pets for adoption and get the contact information of their owners.
- **Market:** Anyone who wants to adopt a pet or post one for adoption.
- **Habit:** Users can look at pets at any time of the day. They can keep scrolling until they find one they like or check regularly at different times to wait for new pets to be posted.
- **Scope:** The first version aims to be a MVP and be fully functional for users. The second version aims to have optional stories that improve user experience and add some convenient features.

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* Users must be able to sign up and create a new profile.
* Users must be able to log in and log out.
* Once users are logged in, they can see the most recent pets posted for adoption.
* Users can scroll and view all Pets posted for adoption indefinitely.
    * Users can choose the source of the queried pets: mixed, from the Parse API, or from PetFinder exclusively.
    * Users can pull down to refresh the most recent pets for adoption.
    * Users can filter pets.
       * By type of animal.
       * By location
* Users can click on a Pet item and see a more detailed view of it.
    * In the detailed Pet view, users can see the owner's contact information, including an approximate location through an embedded map (using Google maps).
    * The app uses an animation to display the detailed view of a Pet.
* Users can post a Pet for adoption.
    * When a Pet is posted, it should be visible in the most recent pets for adoption.
    * Users can edit or delete their posted Pets.
    * Users can see their posted Pets.
* Users can like or unlike a Pet.
    * Users can like or unlike a Pet by double-tapping their detailed view.
* Make sure the app uses a library to add visual polish.


**Optional Nice-to-have Stories**
* Users can see their own profile information.
* Users can edit their profile and add a photo.
* Users can bookmark pet posts.
* Users can see bookmarked pets.
* Users can see Pets near them in Map View and can click on the Pet page to go to their PetDetails page.
* Users can share a pet's information.
* Users can comment a Pet post.
* Users can see other people's profiles.
* Users can sign up and login using Facebook or other external provider.
* Show an indeterminate progress bar while a network request is being performed.
* Improve the app design.


### 2. Screen Archetypes

* Login Screen
   * Users must be able to log in and log out.
   * Users can sign up and login using Facebook. (optional)
* Registration Screen
   * Users must be able to sign up and create a new profile.
   * Users can sign up and log in using Facebook. (optional)
* Stream
    * Show an indeterminate progress bar while a network request is being performed. (optional)
    * Users can see bookmarked pets. (optional)
    * Once users are logged in, they can see the most recent pets posted for adoption.
        * Users can scroll and view all Pets posted for adoption.
        * Users can pull down to refresh the most recent pets for adoption.
* Detail
    * Users can bookmark pet posts. (optional)
    * Users can see bookmarked pets. (optional)
    * Users can bookmark pet posts. (optional)
    * Users can share a pet's information. (optional)
    * Users can like or unlike a Pet.
        * Users can like or unlike a Pet by double-tapping their detailed view.
    * Users can click on a Pet item and see a more detailed view of it.
        * In the detailed Pet view, users can see the owner's contact information, including an approximate location through an embedded map (using Google maps).
        * The app uses an animation to display the detailed view of a Pet.
* Creation
    * Users can comment a Pet post. (optional)
    * Post pet functionality is built using modal overlay. (optional)
    * Users can post a Pet for adoption.
        * When a Pet is posted, it should be visible in the most recent pets for adoption.
* Profile
    * Users can see their own profile information. (optional)
    * Users can edit their profile and add a photo. (optional)
    * Users can see other people's profiles. (optional)
* Search
    * Users can filter pets.
        * By type of animal.
        * By location


### 3. Navigation

**Tab Navigation** (Tab to Screen)
* Main Screen
* Bookmarked pets (optional)

**Flow Navigation** (Screen to Screen)

* Initial Screen
    * => Login
    * => Signup
* Login
   * => Main Screen
   * => Initial Screen
* Signup
   * => Main Screen
   * => Initial Screen
* Main Screen
   * => Post new Pet
   * => Detailed Pet
   * => Bookmarked Pets (optional)
   * => My profile
   * => Initial Screen (logout)
* Post new Pet
    * => Main Screen
* Detailed Pet
    * => Main Screen
    * => Poster profile (optional)
    * => Bookmarked Pets (optional, and only when the user clicked the Pet from that screen)
* Bookmarked Pets (optional)
    * => Detailed Pet
    * => Main Screen
    * => Initial Screen (logout)
* Poster profile (optional)
    * => Detailed Pet
* My profile (optional)
    * => Main Screen
    * => Bookmarked Pets (optional)

## Wireframes
<img src="https://github.com/armandouv/AdoptMe/blob/main/w1.jpg" width=600>
<img src="https://github.com/armandouv/AdoptMe/blob/main/w2.jpg" width=600>
<img src="https://github.com/armandouv/AdoptMe/blob/main/w3.jpg" width=600>

## Schema
[This section will be completed in Unit 9]
### Models
[Add table of models]
### Networking
- [Add list of network requests by screen ]
- [Create basic snippets for each Parse network request]
- [OPTIONAL: List endpoints if using existing API such as Yelp]
