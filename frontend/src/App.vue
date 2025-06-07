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
  background: #000000;
  color: #EAEAEA;
}

#app, .app-container {
  height: 100%;
  width: 100%;
}

/* Dark-themed scrollbar */
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.05);
}

::-webkit-scrollbar-thumb {
  background: #444;
  border-radius: 6px;
  transition: background 0.2s ease-in-out;
}

::-webkit-scrollbar-thumb:hover {
  background: #666;
}

/* Global input focus outline */
input:focus, textarea:focus, button:focus {
  outline: none;
  box-shadow: 0 0 0 2px rgba(67, 100, 247, 0.4); /* Subtle focus ring matching the theme */
}
button:focus {
  box-shadow: none; /* Buttons have their own focus styles */
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