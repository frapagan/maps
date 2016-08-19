#occurrence {
  dot-width: 2;
  [total <= 10] { dot-fill: #FFFF00;  }
  [total > 10][total <= 100] { dot-fill: #FFCC00;  }
  [total > 100][total <= 1000] { dot-fill: #FF9900;  }
  [total > 1000][total <= 10000] { dot-fill: #FF6600;  }
  [total > 10000] { dot-fill: #D60A00;  }
}

#2occurrence {
  dot-width: 4;
  [total <= 5] { dot-fill: #5e0063;  }
  [total > 5][total <= 10] { dot-fill: #851362;  }
  [total > 10][total <= 50] { dot-fill: #a42e61;  }
  [total > 50][total <= 100] { dot-fill: #be4c60;  }
  [total > 100][total <= 500] { dot-fill: #d26b63;  }
  [total > 500][total <= 1000] { dot-fill: #e28b6b;  }
  [total > 1000][total <= 50000] { dot-fill: #eeab79;  }
  [total > 5000][total <= 10000] { dot-fill: #f7cb8e;  }
  [total > 10000] { dot-fill: #ffebaa;  }
}
