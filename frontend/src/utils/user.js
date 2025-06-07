const USER_ID_KEY = 'haigui_user_id';

/**
 * 获取或创建一个永久性的匿名用户ID。
 * 如果localStorage中不存在，则会创建一个新的ID并存储。
 * @returns {string} 用户的唯一ID。
 */
export const getUserId = () => {
  let userId = localStorage.getItem(USER_ID_KEY);
  if (!userId) {
    userId = `user_${Date.now()}_${Math.random().toString(36).substring(2, 10)}`;
    localStorage.setItem(USER_ID_KEY, userId);
  }
  return userId;
};

// 立即确保用户有一个ID
getUserId(); 