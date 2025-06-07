<template>
  <div class="app-container">
    <router-view v-slot="{ Component }">
      <transition name="fade" mode="out-in">
        <component :is="Component" />
      </transition>
    </router-view>
  </div>
</template>

<script setup>
// No script needed for this setup
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body {
  height: 100vh;
  width: 100vw;
  overflow: hidden; /* Prevent body scroll */
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  background: #F7F7F7; /* Use a slightly off-white for a softer feel */
  color: #1a1a1a; /* Dark gray for text, not pure black */
}

#app, .app-container {
  height: 100%;
  width: 100%;
}

/* Light-themed scrollbar */
::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

::-webkit-scrollbar-track {
  background: #EFEFEF;
}

::-webkit-scrollbar-thumb {
  background: #D1D1D1;
  border-radius: 8px;
  border: 2px solid #EFEFEF; /* Add padding around thumb */
  transition: background 0.2s ease-in-out;
}

::-webkit-scrollbar-thumb:hover {
  background: #B1B1B1;
}

/* Subtle focus ring for accessibility */
input:focus-visible, textarea:focus-visible, button:focus-visible {
  outline: none;
  box-shadow: 0 0 0 3px rgba(0, 122, 255, 0.25);
}
button:focus-visible {
  box-shadow: 0 0 0 3px rgba(0, 122, 255, 0.25);
}


/* Global page transition */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>